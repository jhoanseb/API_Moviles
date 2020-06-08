package controllers

import javax.inject._
import play.api._
import play.api.mvc._

import org.json4s._
import org.json4s.native.JsonMethods

import play.api.libs.json._
import play.api.libs.functional.syntax._

import models._
import neo4j.PublicacionesRepo

@Singleton
class PublicacionesController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {
  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def getSeguidos(idUsuario : Int) = Action { implicit request: Request[AnyContent] =>
    implicit val formats = DefaultFormats
    var result = PublicacionesRepo.GetPublicacionesDeSeguidos(idUsuario)
    Ok(result)
  }
  def getUsuario(idUsuario : Int) = Action { implicit request: Request[AnyContent] =>
    implicit val formats = DefaultFormats
    var result = PublicacionesRepo.GetPublicacionesDeUsuario(idUsuario)
    Ok(result)
  }

    implicit val CreatePublicacionModelReads: Reads[CreatePublicacionModel] = (
        (JsPath \ "idUsuario").read[Int] and
        (JsPath \ "Tipo").read[Int] and
        (JsPath \ "Archivo").read[String] and
        (JsPath \ "Descripcion").read[String]
    )(CreatePublicacionModel.apply _)

    def create = Action { request =>
        request.body.asJson.map { json =>
        json.validate[CreatePublicacionModel].map{ 
            case (createPublicacionModel) =>  
            var publicacion = new PublicacionModel(createPublicacionModel)          
            val res = PublicacionesRepo.CreatePublicacion(publicacion)
            Ok("Solicitud exitosa")
        }.recoverTotal{
            e => BadRequest("Json Error")
        }
        }.getOrElse {
        BadRequest("Expecting Json data")
        }
    }

    implicit val DeletePublicacionReads: Reads[DeletePublicacion] = (
        (JsPath \ "idUsuario").read[Int] and
        (JsPath \ "idPublicacion").read[Int] 
    )(DeletePublicacion.apply _)

  def delete = Action { request =>
      request.body.asJson.map { json =>
      json.validate[DeletePublicacion].map{ 
          case (deletePublicacion) =>  
          val res = PublicacionesRepo.DeletePublicacion(deletePublicacion)
          Ok("Solicitud exitosa")
      }.recoverTotal{
          e => BadRequest("Json Error")
      }
      }.getOrElse {
      BadRequest("Expecting Json data")
      }
  }
  //Likes
  def getLikes(idPublicacion : Int) = Action { implicit request: Request[AnyContent] =>
    implicit val formats = DefaultFormats
    var result = PublicacionesRepo.GetLikesDePublicacion(idPublicacion)
    Ok(result)
  }

    implicit val CreateLikeReads: Reads[CreateLike] = (
        (JsPath \ "idUsuario").read[Int] and
        (JsPath \ "idPublicacion").read[Int] 
    )(CreateLike.apply _)

    def createLike = Action { request =>
        request.body.asJson.map { json =>
        json.validate[CreateLike].map{ 
            case (createLike) =>  
            val res = PublicacionesRepo.CreateLike(createLike)
            Ok("Solicitud exitosa")
        }.recoverTotal{
            e => BadRequest("Json Error")
        }
        }.getOrElse {
        BadRequest("Expecting Json data")
        }
    }

    implicit val DeleteLikeReads: Reads[DeleteLike] = (
        (JsPath \ "idUsuario").read[Int] and
        (JsPath \ "idPublicacion").read[Int] 
    )(DeleteLike.apply _)

    def deleteLike = Action { request =>
        request.body.asJson.map { json =>
        json.validate[DeleteLike].map{ 
            case (deleteLike) =>  
            val res = PublicacionesRepo.DeleteLike(deleteLike)
            Ok("Solicitud exitosa")
        }.recoverTotal{
            e => BadRequest("Json Error")
        }
        }.getOrElse {
        BadRequest("Expecting Json data")
        }
    }
    
  //Etiquetas

    implicit val CreateEtiquetaReads: Reads[CreateEtiqueta] = (
        (JsPath \ "idUsuario").read[Int] and
        (JsPath \ "idPublicacion").read[Int] 
    )(CreateEtiqueta.apply _)

    def createEtiqueta = Action { request =>
        request.body.asJson.map { json =>
        json.validate[CreateEtiqueta].map{ 
            case (createEtiqueta) =>  
            val res = PublicacionesRepo.CreateEtiqueta(createEtiqueta)
            Ok("Solicitud exitosa")
        }.recoverTotal{
            e => BadRequest("Json Error")
        }
        }.getOrElse {
        BadRequest("Expecting Json data")
        }
    }
    def getEtiquetas(idPublicacion : Int) = Action { implicit request: Request[AnyContent] =>
        implicit val formats = DefaultFormats
        var result = PublicacionesRepo.GetEtiquetasDePublicacion(idPublicacion)
        Ok(result)
    }

    implicit val DeleteEtiquetaReads: Reads[DeleteEtiqueta] = (
        (JsPath \ "idUsuario").read[Int] and
        (JsPath \ "idPublicacion").read[Int] 
    )(DeleteEtiqueta.apply _)

    def deleteEtiqueta = Action { request =>
        request.body.asJson.map { json =>
        json.validate[DeleteEtiqueta].map{ 
            case (deleteEtiqueta) =>  
            val res = PublicacionesRepo.DeleteEtiqueta(deleteEtiqueta)
            Ok("Solicitud exitosa")
        }.recoverTotal{
            e => BadRequest("Json Error")
        }
        }.getOrElse {
        BadRequest("Expecting Json data")
        }
    }
}
