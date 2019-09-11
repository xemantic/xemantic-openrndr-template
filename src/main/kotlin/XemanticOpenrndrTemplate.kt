package com.xemantic.openrndr.template

import com.xemantic.openrndr.core.Format
import com.xemantic.openrndr.core.ScreenOutput
import com.xemantic.openrndr.core.XemanticContext
import org.openrndr.application

fun main() = application {
  val context = XemanticContext(
      format = Format.FULLSCREEN
  )
  configure {
    context.configure(this)
  }
  program {
    extend(context)
    val feedbackBuffer = context.colorBuffer()
    val feedbackShader = context.fragmentShader("Feedback.frag")
    extend {
      feedbackShader.apply(feedbackBuffer, feedbackBuffer)
    }
    extend(ScreenOutput(feedbackBuffer))
  }
}
