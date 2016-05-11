package com.example

import org.specs2.concurrent.ExecutionEnv
import org.specs2.mutable.Specification

import scala.concurrent.ExecutionContext.Implicits.global

class FutureRepositorySpec extends Specification with TestHelper with TestDb {

  "A FutureUserRepository" should {

    "be able to operate with Futures" in { implicit ee: ExecutionEnv =>
      (for {
        r  <- futureDb("test_future_repo")
        c1 <- r.create(user1)
        c2 <- r.create(user2)
        u  <- r.update(user1updated)
        a1 <- r.findAll()
        o  <- r.findOne(user1.id)
        d1 <- r.delete(user1.id)
        d2 <- r.delete(user2.id)
        a2  <- r.findAll()
      } yield (o.map { _.username }, c1, c2, a1.size, a2.size, u, d1, d2)) must equalTo(Some("user1updated"), 1, 1, 2, 0, 1, 1, 1).await
    }

  }

}

