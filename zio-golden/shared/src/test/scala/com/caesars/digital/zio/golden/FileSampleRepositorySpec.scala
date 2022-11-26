package com.caesars.digital.zio.golden

import zio.*
import zio.nio.file.*
import zio.test.*

import com.caesars.digital.zio.golden.SampleRepository

object FileSampleRepositorySpec extends ZIOSpecDefault {

  case class Test1(s: String)
  case class Test2(s: String)

  override def spec =
    suite("File Sample repository specification")(
      test("Read back the same strings that were written") {
        check(Gen.setOf(Gen.alphaNumericStringBounded(10, 100))) { strings =>
          for {
            ids             <- ZIO.foreach(strings)(SampleRepository.write[Path, String, Test1])
            restoredStrings <- ZIO.foreach(ids)(SampleRepository.read[Path, String])
            _               <- SampleRepository.removeAll[Path, String, Test1]
          } yield assertTrue(strings == restoredStrings)
        }
      },
      test("Files for different case classes do not clash") {
        check(Gen.setOf(Gen.alphaNumericStringBounded(10, 100))) { strings =>
          for {
            _    <- ZIO.foreach(strings)(SampleRepository.write[Path, String, Test1])
            _    <- ZIO.foreach(strings)(SampleRepository.write[Path, String, Test2])
            ids1 <- SampleRepository.readAllIds[Path, String, Test1]
            ids2 <- SampleRepository.readAllIds[Path, String, Test2]
            _    <- SampleRepository.removeAll[Path, String, Test1]
            _    <- SampleRepository.removeAll[Path, String, Test2]
          } yield assertTrue(ids1.length == strings.size) && assertTrue(ids2.length == strings.size)
        }
      }
    ).provide(FileSampleRepository.live) @@ TestAspect.sequential @@ TestAspect.samples(10)
}
