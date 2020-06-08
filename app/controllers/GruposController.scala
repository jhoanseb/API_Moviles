package controllers

import javax.inject._
import play.api._
import play.api.mvc._

import org.json4s._
import org.json4s.native.JsonMethods

import play.api.libs.json._
import play.api.libs.functional.syntax._

import models._
import neo4j.GruposRepo

@Singleton
class GruposController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {
  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
    def get(idUsuario : Int) = Action { implicit request: Request[AnyContent] =>
        implicit val formats = DefaultFormats
        var result = GruposRepo.GetGrupos(idUsuario)
        Ok(result)
    }

    implicit val CreateGrupoModelReads: Reads[CreateGrupoModel] = (
        (JsPath \ "idUsuario").read[Int] and
        (JsPath \ "Tema").read[String] and
        (JsPath \ "Nombre").read[String]
    )(CreateGrupoModel.apply _)
    
    def create = Action { request =>
        request.body.asJson.map { json =>
        json.validate[CreateGrupoModel].map{ 
            case (createGrupoModel) =>          
            val res = GruposRepo.CreateGrupo(createGrupoModel)
            Ok("Solicitud exitosa")
        }.recoverTotal{
            e => BadRequest("Json Error")
        }
        }.getOrElse {
        BadRequest("Expecting Json data")
        }
    }

    implicit val AddUsuarioGrupoModelReads: Reads[AddUsuarioGrupoModel] = (
        (JsPath \ "idUsuario").read[Int] and
        (JsPath \ "idGrupo").read[Int]
    )(AddUsuarioGrupoModel.apply _)

    def addUsuarioGrupo = Action { request =>
        request.body.asJson.map { json =>
        json.validate[AddUsuarioGrupoModel].map{ 
            case (addUsuarioGrupoModel) =>          
            val res = GruposRepo.AgregarUsuarioGrupo(addUsuarioGrupoModel)
            Ok("Solicitud exitosa")
        }.recoverTotal{
            e => BadRequest("Json Error")
        }
        }.getOrElse {
        BadRequest("Expecting Json data")
        }
    }

    implicit val CreateMensajeGrupoReads: Reads[CreateMensajeGrupo] = (
        (JsPath \ "idUsuario").read[Int] and
        (JsPath \ "idGrupo").read[Int] and
        (JsPath \ "Cuerpo").read[String]
    )(CreateMensajeGrupo.apply _)

    def createMensajeGrupo = Action { request =>
        request.body.asJson.map { json =>
        json.validate[CreateMensajeGrupo].map{ 
            case (createMensajeGrupo) =>          
            val res = GruposRepo.CreateMensajeGrupo(createMensajeGrupo)
            if(res==0){
                BadRequest("No se puedo enviar el mensaje")
            }else{
                Ok("Solicitud exitosa")
            }
        }.recoverTotal{
            e => BadRequest("Json Error")
        }
        }.getOrElse {
        BadRequest("Expecting Json data")
        }
    }
    def getMensajes(idGrupo : Int) = Action { implicit request: Request[AnyContent] =>
        implicit val formats = DefaultFormats
        var result = GruposRepo.GetMensajesGrupo(idGrupo)
        Ok(result)
    }

    implicit val RemoveUsuarioGrupoModelReads: Reads[RemoveUsuarioGrupoModel] = (
        (JsPath \ "idUsuario").read[Int] and
        (JsPath \ "idGrupo").read[Int]
    )(RemoveUsuarioGrupoModel.apply _)

    def removeUsuarioGrupo = Action { request =>
        request.body.asJson.map { json =>
        json.validate[RemoveUsuarioGrupoModel].map{ 
            case (removeUsuarioGrupoModel) =>          
            val res = GruposRepo.SacarUsuarioGrupo(removeUsuarioGrupoModel)
            Ok("Solicitud exitosa")
        }.recoverTotal{
            e => BadRequest("Json Error")
        }
        }.getOrElse {
        BadRequest("Expecting Json data")
        }
    }
}
