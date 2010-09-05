/*********************************************************************
  This file is part of tag2tree, see <https://bitbucket.org/mira/tag2tree>

  Copyright (C) 2010 Michael Rauch

  tag2tree is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  tag2tree is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with tag2tree.  If not, see <http://www.gnu.org/licenses/>.
 *********************************************************************/

package ch.miranet.tag2tree.impl

import scala.collection.mutable.ListBuffer

import ch.miranet.tag2tree.TaggedItem
import ch.miranet.tag2tree.TreeNode
import ch.miranet.tag2tree.TreeBuilder
import ch.miranet.tag2tree.Tag

class DefaultTreeBuilder[T <: TaggedItem] extends TreeBuilder[T] {

  def buildTree(tagIndex: Map[Tag, List[T]]): TreeNode[T] = {
    var allItems = Set.empty[T]
    
    // this is only a workaround until code cleanup is done: mutable VS. immutable etc.
    var tMap = Map.empty[Tag, Set[T]]
    
    tagIndex.foreach(entry => {
      val tag = entry._1
      var items = entry._2
      
      allItems = allItems ++ items
      
      val itemSet = Set() ++ items
      tMap = tMap + (tag -> itemSet)
    })
    
    val rootTags = findDividingTags(tMap)    
    val rootNode = new DefaultTreeNode(null, null, Nil, rootTags, List() ++ allItems)
    
    refineNode(rootNode, tMap, Set.empty ++ rootTags)
    rootNode
  }
  
  /**
   * Create child nodes of navigation tree recursively 
   */
  private def refineNode(parentNode: DefaultTreeNode[T], tMap: Map[Tag, Set[T]], dividingTags: Set[Tag]) {
    for (tag <- dividingTags) {
      val subTagMap = reduceTagMap(tMap, tag)
   
      val selectedTags = new ListBuffer[Tag]
      selectedTags ++ parentNode.breadcrumbs
      selectedTags + tag
      
      val subDividingTags = List() ++ findDividingTags(subTagMap)
      val items = List() ++ tMap(tag)
      
      val node = new DefaultTreeNode(parentNode, tag, selectedTags.toList, subDividingTags, items)        
      refineNode(node, subTagMap, Set() ++ subDividingTags)
    }
  }
  
  /**
   * Retrieves the tag with the most match counts
   */
  private def mostPopularTag(tagMatchCounts: List[(Tag, Int)]): Tag = {
    var popTag: Tag = null
    var popTagMatchCount = 0
    tagMatchCounts.foreach ( entry => {
      val tag = entry._1
      val matchCount = entry._2
      if (matchCount > popTagMatchCount) {
        popTag = tag
        popTagMatchCount = matchCount
      }
    })
    popTag
  }
  

  /**
   * Creates a List of tuples (tag, matchCount) by filtering out the given tags AND all items matching (at least) one of the tagsToFilter from the map
   */
  private def tagMatchCounts(tMap: Map[Tag, Set[T]], tagsToFilter: List[Tag], tagsToIgnore: List[Tag]): List[(Tag, Int)] = {
    var result = List[(Tag, Int)]()
    
    tMap.foreach( entry => {
      val tag = entry._1
      var items = Set() ++ entry._2
      if ( ! tagsToFilter.contains(tag) && ! tagsToIgnore.contains(tag)) {
        tagsToFilter.foreach(tagToFilter => items = items -- tMap(tagToFilter))
        result = (tag, items.size) :: result
      }
    }
    )

    return result
  }
    
  /**
   * Creates a Map that contains only items matching the given tag, but the mapping of the tag itself is removed.
   */
  private def reduceTagMap(tMap: Map[Tag, Set[T]], tagToMatch: Tag): Map[Tag, Set[T]] = {      
    var result = Map[Tag, Set[T]]()
    val matchingItems = tMap(tagToMatch)
    
    tMap.foreach( entry => {
      val tag = entry._1
      val items = entry._2
      
      var reducedItems = Set() ++ items
      reducedItems = reducedItems.intersect(matchingItems)
        
      if ( ! reducedItems.isEmpty) {
        result = result + (tag -> reducedItems)
      }
    }
    )

    return result - tagToMatch
  }

  /**
   * Find the tags that divide items most efficiently
   */
  def findDividingTags(tMap: Map[Tag, Set[T]]): List[Tag] = {
    var resultTags = List[Tag]()
    var processedTags = List[Tag]() 
   
    var popTag = mostPopularTag(tagMatchCounts(tMap, resultTags, processedTags))
   
    while (popTag != null) {
      
      // discard popTag if it doesn't narrow down the item-list 
      var allItemsInScope = Set[T]()
      tMap.foreach(allItemsInScope ++= _._2)
      processedTags = popTag :: processedTags
      if ( ! (allItemsInScope -- tMap(popTag) isEmpty)) {
        resultTags = popTag :: resultTags
      }
      val tMatchCounts = tagMatchCounts(tMap, resultTags, processedTags)
      popTag = mostPopularTag(tMatchCounts)
    }
   
    resultTags.reverse
  } 
  
}
