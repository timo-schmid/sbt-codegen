package com.example.validation

import svalidate._

object ValidPassword extends Validator[Array[Byte]] {

  def validate(pw: Array[Byte]): Seq[String] =
    if(pw.length == 32)
      Seq()
    else
      Seq("The password is not a valid SHA-256 hash.")

}
