package models

case class CreatePublicacionModel(var idUsuario: Int, var Tipo: Int, var Archivo: String, var Descripcion: String)
case class CreateLike(var idUsuario: Int, var idPublicacion: Int)
case class DeleteLike(var idUsuario: Int, var idPublicacion: Int)
case class CreateEtiqueta(var idUsuario: Int, var idPublicacion: Int)
case class DeleteEtiqueta(var idUsuario: Int, var idPublicacion: Int)
case class DeletePublicacion(var idUsuario: Int, var idPublicacion: Int)

class PublicacionModel(val publicacion : CreatePublicacionModel){
    private var _idUsuario = publicacion.idUsuario
    private var _Tipo = publicacion.Tipo
    private var _Archivo = publicacion.Archivo
    private var _Descripcion = publicacion.Descripcion

    def idUsuario = _idUsuario
    def idUsuario_= (newValue: Int): Unit = { 
        _idUsuario = newValue 
    }
    def Tipo = _Tipo
    def Tipo_= (newValue: Int): Unit = { 
        _Tipo = newValue 
    }
    def Archivo = _Archivo
    def Archivo_= (newValue: String): Unit = { 
        _Archivo = newValue 
    }
    def Descripcion = _Descripcion
    def Descripcion_= (newValue: String): Unit = { 
        _Descripcion = newValue 
    }
}