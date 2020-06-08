package models

case class CreateComentarioModel(var idPublicacion: Int , var idUsuario: Int ,var Cuerpo: String)
case class DeleteComentario(var idUsuario: Int , var idComentario: Int ,var idPublicacion: Int)

class ComentarioModel(var PCuerpo: String){
    private var _Cuerpo = PCuerpo

    def Cuerpo = _Cuerpo
    def Cuerpo_= (newValue: String): Unit = { 
        _Cuerpo = newValue 
    }
}