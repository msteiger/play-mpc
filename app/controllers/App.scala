package controllers

import play.api.mvc._
import play.api.Logger
import play.api.libs.json._
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.iteratee._

import java.util.concurrent.atomic.AtomicLong

object App extends Controller {

  // Concurrent.broadcast returns (Enumerator, Concurrent.Channel)
  private lazy val (out, channel) = Concurrent.broadcast[JsValue]

  private val noOfUsers = new AtomicLong(0)

  def sockHandler = WebSocket.using[JsValue] { request =>
    Logger.info(s"New browser connected (${ noOfUsers.incrementAndGet } browsers currently connected)")

    val in = Iteratee.ignore[JsValue].map { _ =>
      Logger.info(s"Browser disconnected (${ noOfUsers.decrementAndGet } browsers currently connected)")
    }

    (in, out)
  }

  def sendWebsocketMessage(kind: String, value: Long): Unit =
    broadcast(kind, JsNumber(value))

  def sendWebsocketMessage(kind: String, value: String): Unit =
    broadcast(kind, JsString(value))

  def broadcast(kind: String, value: JsValue): Unit = {
    val msg = Json.obj(
      "type" -> kind,
      "value" -> value
    )
    Logger.debug(msg.toString)

    channel.push(msg)
  }
}
