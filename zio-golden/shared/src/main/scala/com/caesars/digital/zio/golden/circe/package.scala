package com.caesars.digital.zio.golden

import io.circe.Json
import zio.nio.file.Path
import zio.test.*
import zio.Tag

import com.caesars.digital.zio.*
import com.caesars.digital.zio.golden.*

package object circe {
  implicit def deriveGoldenCodec[C](implicit codec: io.circe.Codec[C]): Codec[C, Json] = new Codec[C, Json] {
    override def encode(x: C): Json = codec(x).deepDropNullValues

    override def decode(x: Json): Either[Throwable, C] = codec.decodeJson(x)
  }

  def testAll[C: Tag](gen: Gen[Sized, C], sampleSize: Int = 20)(implicit
      codec: Codec[C, Json]
  ): Seq[Spec[TestEnvironment, Throwable]] =
    Seq(
      testSerializability(gen),
      testSamples(gen, sampleSize)
    )

  def testSerializability[C: Tag](gen: Gen[Sized, C])(implicit codec: Codec[C, Json]): Spec[Sized, Nothing] =
    golden.testSerializability(gen)

  def testSamples[C: Tag](gen: Gen[Sized, C], sampleSize: Int = 20)(implicit
      codec: Codec[C, Json]
  ): Spec[TestEnvironment, Throwable] =
    golden.testSamples[Path, Json, C](gen, sampleSize).provideCustom(CirceFileSampleRepository.live)
}
