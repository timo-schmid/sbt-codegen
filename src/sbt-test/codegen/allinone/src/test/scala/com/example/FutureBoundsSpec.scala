package com.example

import org.specs2.concurrent.ExecutionEnv
import org.specs2.mutable.Specification
import com.example.user.FutureUserBounds

class FutureBoundsSpec extends Specification with TestHelper with FutureUserBounds {

  "A FutureBound" should {

    "return no errors in a filled user" in { implicit ee: ExecutionEnv =>

      validate(user1) map(_.isEmpty) must equalTo(true).await

    }

    "return errors in a empty user" in { implicit ee: ExecutionEnv =>

      val errors = validate(emptyUser)
      errors map(_.isEmpty) must equalTo(false).await
      errors map(_.size)    must equalTo(5).await

    }

  }

}