package com.caesars.digital.zio.golden.circe

import scala.annotation.nowarn

import com.caesars.digital.zio.golden.SampleRepository
import io.circe.*
import io.circe.generic.semiauto.deriveCodec
import zio.nio.file.*
import zio.test.*
import zio.test.magnolia.DeriveGen

object CirceSpec extends ZIOSpecDefault {

  case class Test(id: Int, name: String, value: Float)

  object Test {
    @nowarn
    implicit val codec: Codec[Test] = deriveCodec[Test]
  }

  def removeAllSamples =
    SampleRepository.removeAll[Path, Json, Test].provide(CirceFileSampleRepository.live).orDie

  override def spec =
    suite("Circe specification")(
      testAll(DeriveGen[Test]),
    ) @@ TestAspect.sequential @@ TestAspect.afterAll(removeAllSamples)

}
