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

package ch.miranet.tag2tree
import ch.miranet.tag2tree.impl.{DefaultTreeBuilder, DefaultTag}

object Tag2TreeTestApp {
  
  def main(args : Array[String]) {
    val items = List(
      item("file0", "foo", "bar", "baz"),
      item("file1", "fox", "bar", "bak"),
      item("file2", "foo", "bao", "baz")
    )
    
    // build tagIndex
    val extractor = new TestTagExtractor(items)
    val tagIndex = extractor.createTagIndex
    tagIndex foreach println
      
    // build navigation tree
    val treeBuilder = new DefaultTreeBuilder[TestItem]()
    val rootNode = treeBuilder.buildTree(tagIndex)
    
    println("--------------------------------------------------------")
    printTree(rootNode)
  }
  
  
  def tag(name: String) = new DefaultTag(name)
  def item(name: String, tagNames: String*) = new TestItem(name, tagNames: _*)
  
  def printTree(rootNode: TreeNode[TestItem]) { printTree(rootNode, 0) }  
  def printTree(node: TreeNode[TestItem], depth: Int) {
    var branch = ""
    var indent = "   "
    for (i <- 0 to depth) {
      branch += "    "
      indent += "    "
    }
    
    val spacer = "  >>  "
    println(branch + "|")
    println(branch + "|--" + node.name + spacer + node.availableTags)
    val subIdent = Array.fill(node.name.length + spacer.length) { ' ' }
    println(indent + String.valueOf(subIdent) + node.matchingItems)
    
    for (n <- node.childNodes) printTree(n, depth + 1)
  }
  
  case class TestItem(name: String, tagNames: String*) extends TaggedItem {
  	val tags = (for (tagName <- tagNames) yield tag(tagName)).toList
    override def toString = name
  }
  
  
  
  class TestTagExtractor(val items: List[TestItem]) extends TagExtractor[TestItem] {
    def createTagIndex : Map[Tag, List[TestItem]] = {
      val index = scala.collection.mutable.Map[Tag, List[TestItem]]()
      for (item <- items) {
        for (tag <- item.tags) {
        	if ( ! index.contains(tag)) index += (tag -> List())
          index(tag) = item :: index(tag)
        }
      }
      
      Map.empty ++ index  // return immutable map
    } 
  }
  
}
