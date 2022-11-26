package com.caesars.digital.zio.golden.circe

import com.caesars.digital.zio.golden.{FileSampleRepository, SampleRepository}
import io.circe.*
import io.circe.parser.parse
import zio.*
import zio.nio.file.*

final case class CirceFileSampleRepository() extends SampleRepository[Path, Json] {
  val repository = FileSampleRepository()

  override def write[T: Tag](sample: Json): Task[Path] =
    repository.write[T](sample.spaces2)

  override def read(id: Path): Task[Json] =
    repository.read(id).flatMap(str => ZIO.fromEither(parse(str)))

  override def readAllIds[T: Tag]: Task[Seq[Path]] =
    repository.readAllIds[T]

  override def removeAll[T: Tag]: Task[Unit] =
    repository.removeAll[T]
}

object CirceFileSampleRepository {
  val live = ZLayer.succeed(CirceFileSampleRepository())
}
