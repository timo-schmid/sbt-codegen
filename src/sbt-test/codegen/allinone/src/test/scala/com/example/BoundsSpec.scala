package com.example

import org.specs2.mutable.Specification
import com.example.user.UserBounds

class BoundsSpec extends Specification with TestHelper with UserBounds {

  "A Bound" should {

    "return no errors in a filled user" in {

      validate(user1).isEmpty must equalTo(true)

    }

    "return errors in a empty user" in {

      val errors = validate(emptyUser)
      errors.isEmpty must equalTo(false)
      errors.size    must equalTo(5)

    }

  }

}