package com.example

import com.example.user.{UserRepository, UserFutureRepository}
import doobie.contrib.h2.h2transactor._
import doobie.imports._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import scalaz.concurrent.Task

trait TestDb {

  class TaskUserRepository(override val xa: Transactor[Task]) extends UserRepository

  class FutureUserRepository(override val xa: Transactor[Task]) extends UserFutureRepository

  def taskDb(dbName: String): Task[TaskUserRepository] =
    for {
      xa <- H2Transactor[Task](s"jdbc:h2:mem:$dbName;DB_CLOSE_DELAY=-1", "sa", "")
      _  <- xa.setMaxConnections(20)
      _  <- xa.trans(up)
      r  =  new TaskUserRepository(xa)
    } yield r

  def futureDb(dbName: String)(implicit ec: ExecutionContext): Future[FutureUserRepository] = Future({
    (for {
      xa <- H2Transactor[Task](s"jdbc:h2:mem:$dbName;DB_CLOSE_DELAY=-1", "sa", "")
      _  <- xa.setMaxConnections(20)
      _  <- xa.trans(up)
      r  =  new FutureUserRepository(xa)
    } yield r).run
  })(ec)

  private val up: ConnectionIO[Int] =
    sql"""
        CREATE TABLE IF NOT EXISTS users (
          id INT PRIMARY KEY AUTO_INCREMENT,
          username VARCHAR(255),
          firstName VARCHAR(255),
          lastName VARCHAR(255),
          email VARCHAR(255) UNIQUE,
          password BINARY(32),
          salt BINARY(4),
          lastLogin DATETIME,
          created DATETIME,
          updated DATETIME
        )
    """
      .update.run

}

