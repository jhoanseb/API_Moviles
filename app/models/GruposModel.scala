package models

case class CreateGrupoModel(var idUsuario: Int, var Tema: String, var Nombre: String)
case class CreateMensajeGrupo(var idUsuario: Int, var idGrupo: Int, var Cuerpo: String)
case class AddUsuarioGrupoModel(var idUsuario: Int, var idGrupo: Int)
case class RemoveUsuarioGrupoModel(var idUsuario: Int, var idGrupo: Int)

class GrupoModel(var PidUsuario: Int, var PTema: String, var PNombre: String){
    private var _idUsuario = PidUsuario
    private var _Tema = PTema
    private var _Nombre = PNombre

    def idUsuario = _idUsuario
    def idUsuario_= (newValue: Int): Unit = { 
        _idUsuario = newValue 
    }
    def Tema = _Tema
    def Tema_= (newValue: String): Unit = { 
        _Tema = newValue 
    }
    def Nombre = _Nombre
    def Nombre_= (newValue: String): Unit = { 
        _Nombre = newValue 
    }
}