package com.whbettingengine.zio.golden

import scala.reflect.runtime.universe.TypeTag

import io.circe.Json
import zio.nio.file.Path
import zio.test._

import com.whbettingengine.zio._
import com.whbettingengine.zio.golden._

package object circe {
  implicit def deriveGoldenCodec[C](implicit codec: io.circe.Codec[C]): Codec[C, Json] = new Codec[C, Json] {
    override def encode(x: C): Json = codec(x).deepDropNullValues

    override def decode(x: Json): Either[Throwable, C] = codec.decodeJson(x)
  }

  def testAll[C: TypeTag](gen: Gen[Sized, C], sampleSize: Int = 20)(implicit
      codec: Codec[C, Json]
  ): Seq[Spec[TestEnvironment, Throwable]] =
    Seq(
      testSerializability(gen),
      testSamples(gen, sampleSize)
    )

  def testSerializability[C: TypeTag](gen: Gen[Sized, C])(implicit codec: Codec[C, Json]): Spec[Sized, Nothing] =
    golden.testSerializability(gen)

  def testSamples[C: TypeTag](gen: Gen[Sized, C], sampleSize: Int = 20)(implicit
      codec: Codec[C, Json]
  ): Spec[TestEnvironment, Throwable] =
    golden.testSamples[Path, Json, C](gen, sampleSize).provideCustom(CirceFileSampleRepository.live)
}
