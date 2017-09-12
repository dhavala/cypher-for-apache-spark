/**
 * Copyright (c) 2016-2017 "Neo4j, Inc." [https://neo4j.com]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.opencypher.caps.ir.api.block

import java.net.URI

import org.opencypher.caps.ir.api._
import org.opencypher.caps.ir.api.pattern.AllGiven

final case class ProjectBlock[E](
  after: Set[BlockRef],
  binds: ProjectedFields[E] = ProjectedFields[E](),
  where: AllGiven[E] = AllGiven[E](),
  source: Option[URI],
  distinct: Boolean = false
) extends BasicBlock[ProjectedFields[E], E](BlockType("project"))

final case class ProjectedFields[E](items: Map[IRField, E] = Map.empty[IRField, E]) extends Binds[E] {
  override def fields = items.keySet
}

case object ProjectedFieldsOf {
  def apply[E](entries: (IRField, E)*) = ProjectedFields(entries.toMap)
}
