package controllers

import javax.inject._
import play.api._
import play.api.mvc._

import org.json4s._
import org.json4s.native.JsonMethods

import play.api.libs.json._
import play.api.libs.functional.syntax._

import models._
import neo4j.MensajesRepo

@Singleton
class MensajesController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {
  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def get(idUsuario1 : Int,idUsuario2 : Int) = Action { implicit request: Request[AnyContent] =>
    implicit val formats = DefaultFormats
    var result = MensajesRepo.GetMensajes(idUsuario1,idUsuario2)
    Ok(result)
  }
  
  implicit val CreateMensajeModelReads: Reads[CreateMensajeModel] = (
    (JsPath \ "idUsuario1").read[Int] and
      (JsPath \ "idUsuario2").read[Int] and
      (JsPath \ "Cuerpo").read[String]
  )(CreateMensajeModel.apply _)

  def create = Action { request =>
      request.body.asJson.map { json =>
      json.validate[CreateMensajeModel].map{ 
          case (createMensajeModel) =>          
          val res = MensajesRepo.CreateMensaje(createMensajeModel)
          if(res== -1){ Ok("Usuario Bloqueado") } else{
          if(res==0){ BadRequest("Detected error in db") }
          else{ Ok("Solicitud exitosa") }}
      }.recoverTotal{
          e => BadRequest("Json Error")
      }
      }.getOrElse {
      BadRequest("Expecting Json data")
      }
  }
  def getChats(idUsuario : Int) = Action { implicit request: Request[AnyContent] =>
    implicit val formats = DefaultFormats
    var result = MensajesRepo.GetChatsIndividuales(idUsuario)
    Ok(result)
  }
}
