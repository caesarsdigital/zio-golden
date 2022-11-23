package com.caesars.digital.zio.golden

import scala.reflect.runtime.universe.TypeTag

import zio.*

trait SampleRepository[SampleId, Sample] {
  def write[CaseClass: TypeTag](sample: Sample): Task[SampleId]

  def read(id: SampleId): Task[Sample]

  def readAllIds[CaseClass: TypeTag]: Task[Seq[SampleId]]

  def removeAll[CaseClass: TypeTag]: Task[Unit]
}

object SampleRepository {
  final def write[I: Tag, S: Tag, C: TypeTag](sample: S): RIO[SampleRepository[I, S], I] =
    ZIO.serviceWithZIO[SampleRepository[I, S]](_.write[C](sample))

  final def read[I: Tag, S: Tag](id: I): RIO[SampleRepository[I, S], S] =
    ZIO.serviceWithZIO[SampleRepository[I, S]](_.read(id))

  final def readAllIds[I: Tag, S: Tag, C: TypeTag]: RIO[SampleRepository[I, S], Seq[I]] =
    ZIO.serviceWithZIO[SampleRepository[I, S]](_.readAllIds[C])

  final def removeAll[I: Tag, S: Tag, C: TypeTag]: RIO[SampleRepository[I, S], Unit] =
    ZIO.serviceWithZIO[SampleRepository[I, S]](_.removeAll[C])
}
