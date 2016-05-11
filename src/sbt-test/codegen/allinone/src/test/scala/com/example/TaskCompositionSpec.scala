package com.example

import org.specs2.mutable.Specification
import com.example.user.TaskUserBounds

class TaskCompositionSpec extends Specification with TestDb with TaskUserBounds with TestHelper {

  sequential

  val emptyMap: Map[String, Seq[String]] = Map[String, Seq[String]]()

  "A TaskBounds and a TaskUserRepository" should {

    "be able to compose operations with Tasks" in {
      (for {
        v1 <- validate(user1)
        v2 <- validate(user2)
        r  <- taskDb("test_composition")
        a1 <- r.findAll()
        o  <- r.findOne(user1.id)
      } yield (v1, v2, a1.size, o)).run must equalTo(
        emptyMap, emptyMap, 0, None
      )
    }

  }

}

