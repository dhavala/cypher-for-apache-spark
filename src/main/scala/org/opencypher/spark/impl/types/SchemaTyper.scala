package org.opencypher.spark.impl.types

import org.neo4j.cypher.internal.frontend.v3_2.ast._
import org.opencypher.spark.api.CypherType
import org.opencypher.spark.api.schema.Schema
import org.opencypher.spark.api.types._

/*
  TODO:

  * [X] Property Lookup
  * [X] Some basic literals
  * [X] List literals
  * [ ] Function application, esp. considering overloading
  * [ ] Stuff which messes with scope
  * [ ] Some operators: +, [], unary minus, AND
  *
  * [ ] Dealing with same expression in multiple scopes
  * [ ] Make sure to always infer all implied labels
  * [ ] Actually using the schema to get list of slots
 */
case class SchemaTyper(schema: Schema) {
  def infer(expr: Expression, tr: TypingResult): TypingResult = {
    tr.bind { tc0: TypeContext =>
      expr match {

        case _: SignedDecimalIntegerLiteral =>
          tc0.updateType(expr -> CTInteger)

        case _: DecimalDoubleLiteral =>
          tc0.updateType(expr -> CTFloat)

        case _: BooleanLiteral =>
          tc0.updateType(expr -> CTBoolean)

        case _: StringLiteral =>
          tc0.updateType(expr -> CTString)

        case _: Null =>
          tc0.updateType(expr -> CTNull)

        case ListLiteral(elts) =>
          elts.foldLeft[TypingResult](tc0) {
            case (innerTc, elt) => infer(elt, innerTc)
          }
          .bind { tc1: TypeContext =>
            val optEltType = elts.foldLeft[Option[CypherType]](None) {
              case (None, elt) => tc1.typeTable.get(elt)
              case (Some(accType), elt) => Some(tc1.joinType(accType, elt))
            }
            val eltType = optEltType.getOrElse(CTVoid)
            tc1.updateType(expr -> CTList(eltType))
          }

        case Property(v: Variable, PropertyKeyName(name)) =>
          tc0.variableType(v) {
            case CTNode(labels) =>
              val keys = labels.map(schema.nodeKeys).reduce(_ ++ _)
              tc0.updateType(expr -> keys(name))

            case CTRelationship(types) =>
              val keys = types.map(schema.relationshipKeys).reduce(_ ++ _)
              tc0.updateType(expr -> keys(name))

            case _ => tc0
          }

        case _ =>
          tc0
      }
    }
  }
}
