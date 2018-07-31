/*
 * Copyright (c) 2016-2018 "Neo4j Sweden, AB" [https://neo4j.com]
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
 *
 * Attribution Notice under the terms of the Apache License 2.0
 *
 * This work was created by the collective efforts of the openCypher community.
 * Without limiting the terms of Section 6, any Derivative Work that is not
 * approved by the public consensus process of the openCypher Implementers Group
 * should not be described as “Cypher” (and Cypher® is a registered trademark of
 * Neo4j Inc.) or as "openCypher". Extensions by implementers or prototypes or
 * proposals for change that have been documented or implemented should only be
 * described as "implementation extensions to Cypher" or as "proposed changes to
 * Cypher that are not yet approved by the openCypher community".
 */
package org.opencypher.okapi.relational.api.table

import org.opencypher.okapi.api.table.CypherTable
import org.opencypher.okapi.api.types.{CTNull, CypherType}
import org.opencypher.okapi.api.value.CypherValue.CypherMap
import org.opencypher.okapi.ir.api.expr.{Aggregator, Expr, Var}
import org.opencypher.okapi.relational.impl.planning.{JoinType, Order}
import org.opencypher.okapi.relational.impl.table.RecordHeader

// TODO: rename to Table
// TODO: document
trait FlatRelationalTable[T <: FlatRelationalTable[T]] extends CypherTable {

  this: T =>

  def cache(): T = this

  def select(cols: String*): T

//  // Convenience for multiple selects + align and union
//  // TODO: provide default implementation
//  def extractEntities(selectExprGroups: Set[Seq[Expr]], baseTableHeader: RecordHeader, targetHeader: RecordHeader)(implicit parameters: CypherMap): T = {
//    selectExprGroups.map { selectExprs =>
//      val selectedColumns = select(selectExprs.map(baseTableHeader.column): _*)
//      selectedColumns
//
//    }
//
//  }

  def filter(expr: Expr)(implicit header: RecordHeader, parameters: CypherMap): T

  def drop(cols: String*): T

  def unionAll(other: T): T

  def orderBy(sortItems: (String, Order)*): T

  def skip(items: Long): T

  def limit(items: Long): T

  def distinct: T

  def distinct(cols: String*): T

  def group(by: Set[Var], aggregations: Set[(Aggregator, (String, CypherType))])(implicit header: RecordHeader, parameters: CypherMap): T

  // TODO: document that this replaces the column if it already exists
  def withColumn(column: String, expr: Expr, preserveNullability: Boolean = true)(implicit header: RecordHeader, parameters: CypherMap): T

  def withColumnRenamed(oldColumn: String, newColumn: String): T

  def join(other: T, joinType: JoinType, joinCols: (String, String)*): T

  def show(rows: Int = 20): Unit
}
