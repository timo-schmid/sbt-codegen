package com.example

import org.specs2.concurrent.ExecutionEnv
import org.specs2.mutable.Specification
import com.example.user.TaskUserBounds

class TaskBoundsSpec extends Specification with TestHelper with TaskUserBounds {

  "A TaskBound" should {

    "return no errors in a filled user" in { implicit ee: ExecutionEnv =>

      validate(user1).run.isEmpty must equalTo(true)

    }

    "return errors in a empty user" in { implicit ee: ExecutionEnv =>

      val errors = validate(emptyUser)
      errors.run.isEmpty must equalTo(false)
      errors.run.size    must equalTo(5)

    }

  }

}