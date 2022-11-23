package com.caesars.digital.zio.golden.circe

import scala.annotation.nowarn

import io.circe.*
import io.circe.generic.semiauto.deriveCodec
import zio.nio.file.*
import zio.test.*
import zio.test.magnolia.DeriveGen

import com.caesars.digital.zio.golden.SampleRepository

object CirceSpec extends ZIOSpecDefault {

  case class Test(id: Int, name: String, value: Float)

  object Test {
    @nowarn
    implicit val codec = deriveCodec[Test]
  }

  def removeAllSamples =
    SampleRepository.removeAll[Path, Json, Test].provide(CirceFileSampleRepository.live).orDie

  override def spec =
    suite("Circe specification")(
      testAll(DeriveGen[Test]),
    ) @@ TestAspect.sequential @@ TestAspect.afterAll(removeAllSamples)

}
