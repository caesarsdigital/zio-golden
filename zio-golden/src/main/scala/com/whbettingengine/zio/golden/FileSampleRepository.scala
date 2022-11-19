package com.whbettingengine.zio.golden

import java.security.MessageDigest
import java.util.Base64
import scala.reflect.runtime.universe.TypeTag

import zio._
import zio.nio.file._

import com.whbettingengine.zio.golden.Reflection

final case class FileSampleRepository() extends SampleRepository[Path, String] {
  private def sampleHash(s: String): String = {
    val md    = MessageDigest.getInstance("SHA-1")
    val bytes = md.digest(s.getBytes)
    Base64.getEncoder.encodeToString(bytes).replace('/', '_')
  }

  private def findTargetPath(path: Path): Task[Path] =
    if (path.toFile.getName == "target") ZIO.succeed(path)
    else path.parent.map(findTargetPath).getOrElse(ZIO.succeed(path))

  private def rootPath: Task[Path] = for {
    current <- ZIO.attemptBlocking(getClass.getResource("/").toURI()).map(Path(_))
    target  <- findTargetPath(current)
    root = target.parent.getOrElse(target)
  } yield root / "src" / "test" / "resources" / "golden"

  private def packagePath[T: TypeTag]: Path = Reflection.typePackage[T].map(Path(_)).reduce(_ / _)

  private def fileNamePrefix[T: TypeTag] = Reflection.typeName[T].mkString("_")

  private def fileName[T: TypeTag](sample: String): String =
    s"${fileNamePrefix[T]}_${sampleHash(sample)}.json"

  private def relativeTestPath[T: TypeTag](sample: String): Path = packagePath[T] / fileName[T](sample)

  override def write[T: TypeTag](sample: String) = {
    val relativePath = relativeTestPath[T](sample)

    for {
      _ <- ZIO
        .fail(new RuntimeException("Cannot store an empty sample"))
        .when(sample.isBlank())

      root <- rootPath
      _    <- Files.createDirectories(root / packagePath[T])
      _    <- Files.writeBytes(root / relativePath, Chunk.fromArray(sample.getBytes("UTF-8")))
    } yield relativePath
  }

  override def read(id: Path): Task[String] = for {
    root  <- rootPath
    bytes <- Files.readAllBytes(root / id)
  } yield new String(bytes.toArray)

  override def readAllIds[T: TypeTag]: Task[Seq[Path]] = for {
    root <- rootPath

    dirExists <- Files.exists(root / packagePath[T])
    ids <-
      if (dirExists)
        Files
          .newDirectoryStream(root / packagePath[T], s"${fileNamePrefix[T]}*")
          .map(packagePath[T] / _.filename)
          .runCollect
      else ZIO.succeed(Seq.empty)
  } yield ids

  override def removeAll[T: TypeTag]: Task[Unit] =
    rootPath.flatMap { root =>
      Files
        .newDirectoryStream(root / packagePath[T], s"${fileNamePrefix[T]}*")
        .runForeach(Files.delete)
    }
}

object FileSampleRepository {
  val live = ZLayer.succeed(FileSampleRepository())
}
