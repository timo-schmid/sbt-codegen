package com.example

import com.example.user.User
import java.util.Date
import java.security.MessageDigest

trait TestHelper {

  def hash(pw: String, salt: String): Array[Byte] =
    MessageDigest
      .getInstance("SHA-256")
      .digest((pw + salt).getBytes)

  val pw = "secret"

  val salt = "1234"

  val user1 = User(
    0,
    "user1",
    "Fname1",
    "Lname1",
    "user1@company.org",
    hash(pw, salt),
    salt.getBytes,
    new Date(),
    new Date(),
    new Date()
  )

  val emptyUser = User(
    0,
    "",
    "",
    "",
    "",
    new Array[Byte](0),
    new Array[Byte](0),
    null,
    null,
    null
  )

  val user1updated = user1.copy(username = "user1updated")

  val user2 = user1.copy(id = 2, username = "user2", email = "user2@company.org")

}

