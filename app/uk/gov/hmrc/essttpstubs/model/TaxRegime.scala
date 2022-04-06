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

import play.api.mvc.PathBindable
import uk.gov.hmrc.essttpstubs.model.Taxid.EmpRef

sealed trait TaxId

object Taxid {
  case class EmpRef(value: String) extends TaxId
}

case class IdType private(ordinal: Int, name: String)

object IdType {

  val EmployeeRef = IdType(0, "EMPREF")

  def idTypeOf(name: String): IdType = name match {
    case "EMPREF" => IdType.EmployeeRef
    case n => throw new IllegalArgumentException(s"$n is not the name of an id type")
  }

  implicit def pathBinder(implicit stringBinder: PathBindable[String]): PathBindable[IdType] = new PathBindable[IdType] {
    override def bind(key: String, value: String): Either[String, IdType] = {
      for {
        idType <- stringBinder.bind(key, value).right
      } yield idTypeOf(idType)
    }
    override def unbind(key: String, regime: IdType): String = {
      regime.name.toLowerCase()
    }
  }
}


sealed trait TaxRegime {
  def name: String

  def taxIdOf(idType: IdType, value: String):  TaxId

}

object TaxRegime {

  implicit def pathBinder(implicit stringBinder: PathBindable[String]): PathBindable[TaxRegime] = new PathBindable[TaxRegime] {
    override def bind(key: String, value: String): Either[String, TaxRegime] = {
      for {
        regime <- stringBinder.bind(key, value).right
      } yield regimeOf(regime)
    }
    override def unbind(key: String, regime: TaxRegime): String = {
      regime.name
    }
  }

  def regimeOf(name: String): TaxRegime = name match {
    case "epaye" => EPaye
    case n => throw new IllegalArgumentException(s"$n is not the name of a tax regime")
  }

  object EPaye extends TaxRegime{
    override def name: String = "Paye"

    def taxIdOf(idType: IdType, value: String):  TaxId = idType match{
      case IdType.EmployeeRef => EmpRef(value)
      case _  => throw new IllegalArgumentException("not a valid id")
    }
  }

}

