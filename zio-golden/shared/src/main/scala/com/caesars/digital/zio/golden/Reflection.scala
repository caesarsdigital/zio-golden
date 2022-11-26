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
    val r = typeNameParts[A].head.split('.').toSeq
    r.take(r.length - 1)
  }

  private def typeRepr[A](implicit tag: Tag[A]): String = tag.tag.repr

  private def typeNameParts[A: Tag]: Seq[String] = typeRepr[A].split("::").toSeq
}
