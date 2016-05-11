package com.example.validation

import svalidate._

object NonEmptyString extends Validator[String] {

  def validate(s: String): Seq[String] =
    if(s == null || s.trim.isEmpty)
      Seq("The string was not empty")
    else
      Seq()

}
