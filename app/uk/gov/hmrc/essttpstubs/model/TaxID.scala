/*
 * Copyright 2022 HM Revenue & Customs
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

package uk.gov.hmrc.essttpstubs.model

import enumeratum.{Enum, EnumEntry}
import play.api.mvc.PathBindable

import scala.collection.immutable

sealed trait TaxID extends EnumEntry

object TaxID extends Enum[TaxID]{
  case class EmpRef(value: String) extends TaxID

  override val values: immutable.IndexedSeq[TaxID] = findValues
}

sealed trait IdType extends EnumEntry

object IdType extends Enum[IdType]{

  case object EmpRef extends IdType

  implicit def pathBinder(implicit stringBinder: PathBindable[String]): PathBindable[IdType] = new PathBindable[IdType] {
    override def bind(key: String, value: String): Either[String, IdType] = {
      for {
        idType <- stringBinder.bind(key, value).right
      } yield IdType.withNameLowercaseOnly(idType)
    }
    override def unbind(key: String, idType: IdType): String = {
      idType.entryName.toLowerCase()
    }
  }

  override val values: immutable.IndexedSeq[IdType] = findValues
}

