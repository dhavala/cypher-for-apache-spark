/*
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
package org.opencypher.caps.api.schema

import org.opencypher.caps.api.types.{CTNode, CTRelationship}
import org.scalatest.{FunSuite, Matchers}

class PropertyKeyMapTest extends FunSuite with Matchers {

  test("property key maps ignore conflict set in equality") {
    val a = PropertyKeyMap(Map.empty)(Set("a"))
    val b = PropertyKeyMap(Map.empty)(Set("b"))
    a should equal(b)
  }

  test("same property key maps with different conflict sets have the same hash code") {
    val a = PropertyKeyMap(Map.empty)(Set("a"))
    val b = PropertyKeyMap(Map.empty)(Set("b"))
    a.hashCode should equal(b.hashCode)
  }

  test("different property key maps are not equal") {
    val a = PropertyKeyMap(Map("a" -> Map("a" -> CTNode)))(Set("a"))
    val b = PropertyKeyMap(Map("a" -> Map("a" -> CTRelationship)))(Set("b"))
    a should not equal(b)
  }

  test("different property key maps have different hash codes") {
    val a = PropertyKeyMap(Map("a" -> Map("a" -> CTNode("a"))))()
    val b = PropertyKeyMap(Map("a" -> Map("a" -> CTRelationship("b"))))()
    a.hashCode should not equal (b.hashCode)
  }

}