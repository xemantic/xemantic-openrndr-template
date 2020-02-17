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
    hideCursor = false
  }
  program {
    extend(context)
    val feedbackShader = context.fragmentShader("Feedback.frag")
    extend {
      feedbackShader.apply(feedbackShader.previousColorBuffer!!, feedbackShader.colorBuffer)
    }
    extend(ScreenOutput(feedbackShader))
  }
}
