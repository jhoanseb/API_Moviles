package models

import play.api.libs.json._
import play.api.libs.functional.syntax._

case class CreateMensajeModel(var idUsuario1: Int, var idUsuario2: Int, var Cuerpo: String)

class MensajeModel(var PidUsuario1: Int, var PidUsuario2: Int, var PCuerpo: String){
    private var _Cuerpo = PCuerpo
    def Cuerpo = _Cuerpo
    def Cuerpo_= (newValue: String): Unit = { 
        _Cuerpo = newValue 
    }
    private var _idUsuario1 = PidUsuario1
    def idUsuario1 = _idUsuario1
    def idUsuario1_= (newValue: Int): Unit = { 
        _idUsuario1 = newValue 
    }
    private var _idUsuario2 = PidUsuario2
    def idUsuario2 = _idUsuario2
    def idUsuario2_= (newValue: Int): Unit = { 
        _idUsuario2 = newValue 
    }
}