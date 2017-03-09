package org.kneelawk.espinteract

import scala.annotation.implicitNotFound

import dispatch.implyRequestHandlerTuple
import scalafx.Includes.handle
import scalafx.application.JFXApp
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.control.Button
import scalafx.scene.layout.Priority
import scalafx.scene.layout.VBox
import scalafx.scene.text.Text

object EspInteract extends JFXApp {
  def setLEDValue(on: Boolean, callback: => Unit) {
    import dispatch._, Defaults._
    import org.json4s._, JsonDSL._, jackson.JsonMethods._

    val u = url("http://192.168.4.1/control")
    val v = u.POST.setContentType("application/json", "UTF-8") << compact(render(("value" -> on)))
    val fres = Http(v OK as.String)
    for (res <- fres) {
      callback
    }
  }

  val statusText = new Text {
    text = "Status: idle"
  }

  def setStatus(status: String) {
    statusText.text = "Status: " + status
  }

  val onButton = new Button {
    text = "On"
    onAction = handle {
      setStatus("sending request")
      setLEDValue(true, setStatus("got response"))
    }
    maxWidth = Double.MaxValue
    maxHeight = Double.MaxValue
    VBox.setVgrow(this, Priority.Always)
  }

  val offButton = new Button {
    text = "Off"
    onAction = handle {
      setStatus("sending request")
      setLEDValue(false, setStatus("got response"))
    }
    maxWidth = Double.MaxValue
    maxHeight = Double.MaxValue
    VBox.setVgrow(this, Priority.Always)
  }

  stage = new JFXApp.PrimaryStage {
    title = "ESP Interact"
    width = 300
    height = 200
    scene = new Scene {
      root = new VBox {
        padding = Insets(5)
        spacing = 5
        children = Seq(
          onButton,
          offButton,
          statusText)
      }
    }
    onCloseRequest = handle {
      import dispatch.Http
      Http.shutdown()
    }
  }
}