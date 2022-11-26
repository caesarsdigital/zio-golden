package com.caesars.digital.zio.golden

import scala.annotation.nowarn
import zio.Tag

object Reflection {
  @nowarn
  def typeName[A: Tag]: Seq[String] = {
    val head +: tail = typeNameParts[A]
    head.split('.').last +: tail
  }

  def typePackage[A: Tag]: Seq[String] = {
    val packageAndClassName = typeNameParts[A].head.split('.').toSeq
    packageAndClassName.take(packageAndClassName.length - 1)
  }

  /** @param tag
    *   zio.Tag that allows to do reflection using izumi.reflect.macrortti.LightTypeTag
    * @return
    *   representation of type as a String in the following format: 'com.package1.package2.Object1::Object2::ClassName'
    */
  private def typeRepr[A](implicit tag: Tag[A]): String = tag.tag.repr

  private def typeNameParts[A: Tag]: Seq[String] = typeRepr[A].split("::").toSeq
}
