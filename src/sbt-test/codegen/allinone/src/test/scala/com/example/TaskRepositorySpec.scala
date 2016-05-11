package com.example

import org.specs2.mutable.Specification

class TaskRepositorySpec extends Specification with TestHelper with TestDb {

  "A TaskUserRepository" should {

    "be able to operate with Tasks" in {
      (for {
        r  <- taskDb("test_task_repo")
        c1 <- r.create(user1)
        c2 <- r.create(user2)
        u  <- r.update(user1updated)
        a1 <- r.findAll()
        o  <- r.findOne(user1.id)
        d1 <- r.delete(user1.id)
        d2 <- r.delete(user2.id)
        a2 <- r.findAll()
      } yield (o.map { _.username }, c1, c2, a1.size, a2.size, u, d1, d2)).run must equalTo(Some("user1updated"), 1, 1, 2, 0, 1, 1, 1)
    }

  }

}

