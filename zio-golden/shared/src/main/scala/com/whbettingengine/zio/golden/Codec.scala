package com.caesars.digital.zio.golden

trait Codec[C, S] {
  def encode(x: C): S

  def decode(x: S): Either[Throwable, C]
}

object Codec {
  final def encode[C, S](x: C)(implicit codec: Codec[C, S]): S                    = codec.encode(x)
  final def decode[S, C](x: S)(implicit codec: Codec[C, S]): Either[Throwable, C] = codec.decode(x)
}
