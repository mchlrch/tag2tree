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
import ch.miranet.tag2tree.Tag
import ch.miranet.tag2tree.TreeNode

// breadcrumbs ist der decision-path der zu diesem knoten führt -> geordnete Reihenfolge
class DefaultTreeNode[T <: TaggedItem](val parent: DefaultTreeNode[T], val tag: Tag, val breadcrumbs: List[Tag],
                         private val availableTagsUnsorted: List[Tag], private val matchingItemsUnsorted: List[T])
    extends TreeNode[T] {

  private val children:ListBuffer[DefaultTreeNode[T]] = new ListBuffer[DefaultTreeNode[T]]
  if (parent != null) parent.children += this
  
  val availableTags = availableTagsUnsorted.sort( (e1, e2) => (e1 < e2) )
  val matchingItems = matchingItemsUnsorted.sort( (e1, e2) => (e1 < e2))

  lazy val childNodes = children.toList.sort( (e1, e2) => (e1.tag < e2.tag) )
  
}
