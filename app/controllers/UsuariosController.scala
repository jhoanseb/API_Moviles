package controllers

import javax.inject._
import play.api._
import play.api.mvc._

import org.json4s._
import org.json4s.native.JsonMethods

import play.api.libs.json._
import play.api.libs.functional.syntax._

import models._
import neo4j.UsuariosRepo

@Singleton
class UsuariosController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {
  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  
  def get(Apodo : String) = Action { implicit request: Request[AnyContent] =>
    implicit val formats = DefaultFormats
    val result = UsuariosRepo.GetUsuario(Apodo)
    Ok(result)
  }
  
  implicit val CreateUsuarioModelReads = Json.reads[CreateUsuarioModel]
  def create = Action { request =>
    request.body.asJson.map { json =>
    json.validate[CreateUsuarioModel].map{ 
        case (createUsuarioModel) => 
        var usr = new UsuarioCreateModel(createUsuarioModel)             
        val res = UsuariosRepo.CreateUsuario(usr)
        if(res==0){
          BadRequest("Apodo ya existe")
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
  implicit val UpdateUsuarioModelReads: Reads[UpdateUsuarioModel] = (
    (JsPath \ "idUsuario").read[Int] and
    (JsPath \ "Apodo").read[String] and
    (JsPath \ "Nombre").read[String] and
    (JsPath \ "Apellido").read[String] and
    (JsPath \ "FotoPerfil").read[String] and
    (JsPath \ "Estado").read[String] 
  )(UpdateUsuarioModel.apply _)

  def update = Action { request =>
    request.body.asJson.map { json =>
    json.validate[UpdateUsuarioModel].map{ 
        case (updateUsuarioModel) => 
        var usr = new UsuarioUpdateModel(updateUsuarioModel)             
        val res = UsuariosRepo.UpdateUsuario(usr)
        if(res==0){
          BadRequest("Apodo ya existe")
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

  implicit val DeleteUsuarioModelReads = Json.reads[DeleteUsuarioModel]
  def delete = Action { request =>
    request.body.asJson.map { json =>
    json.validate[DeleteUsuarioModel].map{ 
        case (deleteUsuarioModel) =>        
        val res = UsuariosRepo.DeleteUsuario(deleteUsuarioModel.idUsuario.toInt)
        Ok("Solicitud exitosa")
    }.recoverTotal{
        e => BadRequest("Json Error")
    }
    }.getOrElse {
    BadRequest("Expecting Json data")
    }
  }
  //Seguidores

  implicit val CreateSeguirReads: Reads[CreateSeguir] = (
    (JsPath \ "idUsuario1").read[Int] and
    (JsPath \ "idUsuario2").read[Int]
  )(CreateSeguir.apply _)

  def createSeguir = Action { request =>
      request.body.asJson.map { json =>
      json.validate[CreateSeguir].map{ 
          case (createSeguir) => 
          val res = UsuariosRepo.CreateSeguir(createSeguir)
          if(res==0){
            BadRequest("Usuario ya seguido")
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

  implicit val DeleteSeguirReads: Reads[DeleteSeguir] = (
    (JsPath \ "idUsuario1").read[Int] and
    (JsPath \ "idUsuario2").read[Int]
  )(DeleteSeguir.apply _)

  def deleteSeguir = Action { request =>
      request.body.asJson.map { json =>
      json.validate[DeleteSeguir].map{ 
          case (deleteSeguir) => 
          val res = UsuariosRepo.DeleteSeguir(deleteSeguir)
          Ok("Solicitud exitosa")
      }.recoverTotal{
          e => BadRequest("Json Error")
      }
      }.getOrElse {
      BadRequest("Expecting Json data")
      }
  }
  def getSeguidos(idUsuario : Int) = Action { implicit request: Request[AnyContent] =>
    implicit val formats = DefaultFormats
    var result = UsuariosRepo.GetSeguidos(idUsuario)
    Ok(result)
  }
  def getSeguidores(idUsuario : Int) = Action { implicit request: Request[AnyContent] =>
    implicit val formats = DefaultFormats
    var result = UsuariosRepo.GetSeguidores(idUsuario)
    Ok(result)
  }
  //Bloqueados

  implicit val CreateBloquearReads: Reads[CreateBloquear] = (
    (JsPath \ "idUsuario1").read[Int] and
    (JsPath \ "idUsuario2").read[Int]
  )(CreateBloquear.apply _)

  def createBloquear = Action { request =>
    request.body.asJson.map { json =>
    json.validate[CreateBloquear].map{ 
        case (createBloquear) => 
        val res = UsuariosRepo.CreateBloquear(createBloquear)
        if(res==0){
          BadRequest("Usuario ya bloqueado")
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
  def getBloqueados(idUsuario : Int) = Action { implicit request: Request[AnyContent] =>
    implicit val formats = DefaultFormats
    var result = UsuariosRepo.GetBloqueados(idUsuario)
    Ok(result)
  }

  implicit val DeleteBloquearReads: Reads[DeleteBloquear] = (
    (JsPath \ "idUsuario1").read[Int] and
    (JsPath \ "idUsuario2").read[Int]
  )(DeleteBloquear.apply _)

  def deleteBloqueado = Action { request =>
    request.body.asJson.map { json =>
    json.validate[DeleteBloquear].map{ 
        case (deleteBloquear) => 
        val res = UsuariosRepo.DeleteBloquear(deleteBloquear)
        if(res==0){
          BadRequest("Usuario no bloqueado")
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

}
