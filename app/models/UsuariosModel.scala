package models

case class CreateUsuarioModel(var Apodo: String, var Nombre: String, var Apellido: String, var FotoPerfil: String, var Estado: String)
case class UpdateUsuarioModel(var idUsuario: Int, var Apodo: String, var Nombre: String, var Apellido: String, var FotoPerfil: String, var Estado: String)
case class CreateSeguir(var idUsuario1: Int, var idUsuario2: Int)
case class DeleteSeguir(var idUsuario1: Int, var idUsuario2: Int)
case class CreateBloquear(var idUsuario1: Int, var idUsuario2: Int)
case class DeleteBloquear(var idUsuario1: Int, var idUsuario2: Int)
case class DeleteUsuarioModel(var idUsuario: Int)


class UsuarioUpdateModel(val usr : UpdateUsuarioModel){
    private var _idUsuario = usr.idUsuario
    private var _Apodo = usr.Apodo
    private var _Nombre = usr.Nombre
    private var _Apellido = usr.Apellido
    private var _FotoPerfil = usr.FotoPerfil
    private var _Estado = usr.Estado

    def idUsuario = _idUsuario
    def idUsuario_= (newValue: Int): Unit = { 
        _idUsuario = newValue 
    }
    def Apodo = _Apodo
    def Apodo_= (newValue: String): Unit = { 
        _Apodo = newValue 
    }
    def Nombre = _Nombre
    def Nombre_= (newValue: String): Unit = { 
        _Nombre = newValue 
    }
    def Apellido = _Apellido
    def Apellido_= (newValue: String): Unit = { 
        _Apellido = newValue 
    }
    def FotoPerfil = _FotoPerfil
    def FotoPerfil_= (newValue: String): Unit = { 
        _FotoPerfil = newValue 
    }
    def Estado = _Estado
    def Estado_= (newValue: String): Unit = { 
        _Estado = newValue 
    }
}
class UsuarioCreateModel(val usr : CreateUsuarioModel){
    private var _Apodo = usr.Apodo
    private var _Nombre = usr.Nombre
    private var _Apellido = usr.Apellido
    private var _FotoPerfil = usr.FotoPerfil
    private var _Estado = usr.Estado

    def Apodo = _Apodo
    def Apodo_= (newValue: String): Unit = { 
        _Apodo = newValue 
    }
    def Nombre = _Nombre
    def Nombre_= (newValue: String): Unit = { 
        _Nombre = newValue 
    }
    def Apellido = _Apellido
    def Apellido_= (newValue: String): Unit = { 
        _Apellido = newValue 
    }
    def FotoPerfil = _FotoPerfil
    def FotoPerfil_= (newValue: String): Unit = { 
        _FotoPerfil = newValue 
    }
    def Estado = _Estado
    def Estado_= (newValue: String): Unit = { 
        _Estado = newValue 
    }
}