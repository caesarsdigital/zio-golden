package com.caesars.digital.zio.golden

import scala.reflect.runtime.universe.{Symbol, Type, TypeTag}

object Reflection {
  def typeName[A](implicit A: TypeTag[A]): Seq[String] = nameForType(A.tpe)

  def typePackage[A](implicit A: TypeTag[A]): Seq[String] =
    owners(A.tpe).collectFirst { case s if s.isPackage => s.fullName.split('.').toSeq }.getOrElse(Seq.empty)

  private def owners(tpe: Type): Iterator[Symbol] =
    Iterator.iterate(tpe.typeSymbol)(_.owner)

  private def baseSymbols(tpe: Type): Seq[Symbol] =
    owners(tpe).takeWhile(!_.isPackage).toSeq.reverse

  private def nameForType(tpe: Type): Seq[String] = {
    val baseNames = baseSymbols(tpe).map(_.name.decodedName.toString)

    (baseNames ++ tpe.typeArgs.flatMap(nameForType))
  }
}
