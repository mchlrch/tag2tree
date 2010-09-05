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

trait TaggedItem extends Ordered[TaggedItem] {
  def name:String
  def tags: List[Tag]
  
  def compare(that: TaggedItem) = this.name compareTo that.name
}
