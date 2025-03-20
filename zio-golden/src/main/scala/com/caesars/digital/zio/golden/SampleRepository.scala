package com.caesars.digital.zio.golden

import zio.*

trait SampleRepository[SampleId, Sample] {
  def write[CaseClass: Tag](sample: Sample): Task[SampleId]

  def read(id: SampleId): Task[Sample]

  def readAllIds[CaseClass: Tag]: Task[Seq[SampleId]]

  def removeAll[CaseClass: Tag]: Task[Unit]
}

object SampleRepository {
  final def write[I: Tag, S: Tag, C: Tag](sample: S): RIO[SampleRepository[I, S], I] =
    ZIO.serviceWithZIO[SampleRepository[I, S]](_.write[C](sample))

  final def read[I: Tag, S: Tag](id: I): RIO[SampleRepository[I, S], S] =
    ZIO.serviceWithZIO[SampleRepository[I, S]](_.read(id))

  final def readAllIds[I: Tag, S: Tag, C: Tag]: RIO[SampleRepository[I, S], Seq[I]] =
    ZIO.serviceWithZIO[SampleRepository[I, S]](_.readAllIds[C])

  final def removeAll[I: Tag, S: Tag, C: Tag]: RIO[SampleRepository[I, S], Unit] =
    ZIO.serviceWithZIO[SampleRepository[I, S]](_.removeAll[C])
}
