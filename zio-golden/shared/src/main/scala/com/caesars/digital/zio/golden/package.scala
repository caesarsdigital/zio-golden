package com.caesars.digital.zio

import zio.*
import zio.test.Assertion.*
import zio.test.*

import com.caesars.digital.zio.golden.Reflection.*

package object golden {
  def testAll[I: Tag, S: Tag, C: Tag](gen: Gen[Sized, C], sampleSize: Int = 20)(implicit
      codec: Codec[C, S]
  ): Seq[Spec[SampleRepository[I, S] with Sized, Throwable]] =
    Seq(
      testSerializability[S, C](gen),
      testSamples[I, S, C](gen, sampleSize)
    )

  def testSerializability[S, C: Tag](gen: Gen[Sized, C])(implicit codec: Codec[C, S]): Spec[Sized, Nothing] = {
    zio.test.test(s"Test that encode/decode work for the class '${className[C]}'") {
      check(gen)(caseClass => assert(codec.decode(codec.encode(caseClass)))(isRight(equalTo(caseClass))))
    }
  }

  def testSamples[I: Tag, S: Tag, C: Tag](gen: Gen[Sized, C], sampleSize: Int = 20)(implicit
      codec: Codec[C, S]
  ): Spec[SampleRepository[I, S] with Sized, Throwable] =
    zio.test.test(s"Test that decode/encode work for the saved samples of class '${className[C]}'") {
      createSamplesIfNecessary[I, S, C](gen, sampleSize) *> {
        for {
          ids         <- SampleRepository.readAllIds[I, S, C]
          testResults <- ZIO.foreachPar(ids)(testOneSample[I, S, C])
        } yield testResults.reduce(_ && _)
      }
    }

  private def testOneSample[I: Tag, S: Tag, C: Tag](
      sampleId: I
  )(implicit codec: Codec[C, S]): ZIO[SampleRepository[I, S], Throwable, TestResult] = {
    for {
      sample    <- SampleRepository.read[I, S](sampleId)
      caseClass <- ZIO.fromEither(codec.decode(sample))
    } yield assert(codec.encode(caseClass))(equalTo(sample)) ?? s"Assertion faild on $sampleId"
  }.mapError(e => new Exception(s"Exception when processing $sampleId: $e"))

  private def className[C: Tag] = typePackage[C].mkString(".") + "." + typeName[C].mkString(".")

  private def createSamples[I: Tag, S: Tag, C: Tag](gen: Gen[Sized, C], sampleSize: Int)(implicit
      codec: Codec[C, S]
  ): RIO[SampleRepository[I, S] & Sized, Unit] = for {
    samples <- Gen
      .setOfN(sampleSize)(gen)
      .runHead
      .someOrFail(new RuntimeException(s"Cannot generate $sampleSize unique samples using the given generator"))

    _ <- ZIO.foreachParDiscard(samples)(sample => SampleRepository.write[I, S, C](codec.encode(sample)))
  } yield ()

  private def createSamplesIfNecessary[I: Tag, S: Tag, C: Tag](gen: Gen[Sized, C], sampleSize: Int)(implicit
      codec: Codec[C, S]
  ): RIO[SampleRepository[I, S] & Sized, Unit] = for {
    n <- SampleRepository.readAllIds[I, S, C].map(_.length)

    _ <- ZIO
      .fail(
        new RuntimeException(
          s"Expected 0 or $sampleSize sample files, got $n!\nRemove the existing samples if you would like them to be regenerated."
        )
      )
      .when(n != sampleSize && n != 0)

    _ <- {
      Console.printLine(s"\nWarning! The samples for the class '${className[C]}' do not exist!\nCreating new samples...") *>
        createSamples[I, S, C](gen, sampleSize)
    }.when(n == 0)

  } yield ()
}
