package example

import zio.test.*
import zio.test.magnolia.DeriveGen
import zio.Scope

import com.caesars.digital.zio.golden.circe.*

object PersonSpec extends ZIOSpecDefault:
  override def spec: Spec[TestEnvironment & Scope, Any] =
    suite("Person golden (sample) tests")(
      testAll(DeriveGen[Person])
    )
