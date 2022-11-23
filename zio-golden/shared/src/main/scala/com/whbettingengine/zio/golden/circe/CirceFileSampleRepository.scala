package com.caesars.digital.zio.golden.circe

import scala.reflect.runtime.universe.TypeTag

import io.circe.*
import io.circe.parser.parse
import zio.*
import zio.nio.file.*

import com.caesars.digital.zio.golden.{FileSampleRepository, SampleRepository}

final case class CirceFileSampleRepository() extends SampleRepository[Path, Json] {
  val repository = FileSampleRepository()

  override def write[T: TypeTag](sample: Json): Task[Path] =
    repository.write[T](sample.spaces2)

  override def read(id: Path): Task[Json] =
    repository.read(id).flatMap(str => ZIO.fromEither(parse(str)))

  override def readAllIds[T: TypeTag]: Task[Seq[Path]] =
    repository.readAllIds[T]

  override def removeAll[T: TypeTag]: Task[Unit] =
    repository.removeAll[T]
}

object CirceFileSampleRepository {
  val live = ZLayer.succeed(CirceFileSampleRepository())
}