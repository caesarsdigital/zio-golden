package com.whbettingengine.zio.golden.circe

import io.circe._
import io.circe.generic.semiauto.deriveCodec
import zio.nio.file._
import zio.test._
import zio.test.magnolia.DeriveGen

import com.whbettingengine.zio.golden.SampleRepository

object CirceSpec extends ZIOSpecDefault {

  case class Test(id: Int, name: String, value: Float)

  object Test {
    implicit val codec = deriveCodec[Test]
  }

  def removeAllSamples =
    SampleRepository.removeAll[Path, Json, Test].provide(CirceFileSampleRepository.live).orDie

  override def spec =
    suite("Circe specification")(
      testAll(DeriveGen[Test]),
    ) @@ TestAspect.sequential @@ TestAspect.afterAll(removeAllSamples)

}
