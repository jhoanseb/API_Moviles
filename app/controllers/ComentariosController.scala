package controllers

import javax.inject._
import play.api._
import play.api.mvc._

import org.json4s._
import org.json4s.native.JsonMethods

import play.api.libs.json._
import play.api.libs.functional.syntax._

import models._
import neo4j.ComentariosRepo

@Singleton
class ComentariosController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {
  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def get(idPublicacion : Int) = Action { implicit request: Request[AnyContent] =>
    implicit val formats = DefaultFormats
    var result = ComentariosRepo.GetComentariosDePublicacion(idPublicacion)
    Ok(result)
  }

    implicit val CreateComentarioModelReads: Reads[CreateComentarioModel] = (
        (JsPath \ "idPublicacion").read[Int] and
        (JsPath \ "idUsuario").read[Int] and
        (JsPath \ "Cuerpo").read[String]
    )(CreateComentarioModel.apply _)

    def create = Action { request =>
        request.body.asJson.map { json =>
        json.validate[CreateComentarioModel].map{ 
            case (createComentarioModel) =>          
            val res = ComentariosRepo.CreateComentario(createComentarioModel)
            Ok("Solicitud exitosa")
        }.recoverTotal{
            e => BadRequest("Json Error")
        }
        }.getOrElse {
        BadRequest("Expecting Json data")
        }
    }

    implicit val DeleteComentarioReads: Reads[DeleteComentario] = (
        (JsPath \ "idUsuario").read[Int] and
        (JsPath \ "idComentario").read[Int] and
        (JsPath \ "idPublicacion").read[Int]
    )(DeleteComentario.apply _)

    def delete = Action { request =>
        request.body.asJson.map { json =>
        json.validate[DeleteComentario].map{ 
            case (deleteComentario) =>          
            val res = ComentariosRepo.DeleteComentario(deleteComentario)
            Ok("Solicitud exitosa")
        }.recoverTotal{
            e => BadRequest("Json Error")
        }
        }.getOrElse {
        BadRequest("Expecting Json data")
        }
    }
}
