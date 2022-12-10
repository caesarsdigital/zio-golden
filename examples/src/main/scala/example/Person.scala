package example

import io.circe.*
import io.circe.generic.semiauto.deriveCodec

case class ContactDetails(address: String, phone: String)

case class Person(id: Int, name: String, ContactDetails: ContactDetails)

object Person:
  given codec: Codec[Person] = deriveCodec[Person]
