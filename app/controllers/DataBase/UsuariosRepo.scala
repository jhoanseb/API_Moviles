package neo4j

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;
import org.neo4j.driver.Result;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.TransactionWork;
import models._
import services.neo._


object UsuariosRepo extends App {

	def ExisteApodo(Apodo:String):Boolean={
		val session = Neo4j.driver.session
		val script = s"match (n:Usuario {Apodo:'${Apodo}'}) return not(count(n)=0) AS result  "
		val result=session.run(script)
		val record_data = if (result.hasNext()) {
			val record = result.next()
			record.get("result").asBoolean()
	    }else{
	    	false
	    }
		session.close()
		return record_data
	}
	//Usuario
	def CreateUsuario(usr : UsuarioCreateModel):Int={
		if (!(ExisteApodo(usr.Apodo))){
			val session = Neo4j.driver.session
			val script = s"call{match(n:Usuario) return case when max(n.idUsuario) is null then 1 else max(n.idUsuario)+1 end as result} create(:Usuario {idUsuario:result,Apodo:'${usr.Apodo}',Nombre:'${usr.Nombre}',Apellido:'${usr.Apellido}',FotoPerfil:'${usr.FotoPerfil}',Estado:'${usr.Estado}'})"
			val result = session.run(script)
			session.close()
			result.consume().counters().nodesCreated()
		}else{
			0
		}
		
	}
	def GetUsuario(Apodo:String):String={
		val session = Neo4j.driver.session
		val script = s"match (n:Usuario {Apodo:'${Apodo}'}) return n.idUsuario AS idUsuario,n.Apodo As Apodo, n.Nombre AS Nombre, n.Apellido AS Apellido,n.FotoPerfil AS FotoPerfil,n.Estado AS Estado"
		val result=session.run(script)
		val record_data = if (result.hasNext()) {
			val record = result.next()
			s"""{"idUsuario":${record.get("idUsuario").asInt()},"Nombre":"${record.get("Nombre").asString()}","Apellido":"${record.get("Apellido").asString()}","Estado":"${record.get("Estado").asString()}","FotoPerfil":"${record.get("FotoPerfil").asString()}","Apodo":"${record.get("Apodo").asString()}"}"""
	    }else{
	      s"$Apodo no encontrado."
	    }
		session.close()
		return record_data
	}
	def DeleteUsuario(idUsuario:Int):Int={
		val session = Neo4j.driver.session
		val script = s"match ((a:Usuario {idUsuario:$idUsuario})-[:Rpublica]->(p)) detach delete p"//delete publicaciones
		val script1= s"match (a:Usuario {idUsuario:$idUsuario}) detach delete a"//delete nodo usuario
		val script2= s"call{match (:Usuario {idUsuario:$idUsuario})-[:Remisor]->(r:Mensajes)-[:Rreceptor]->(c) return r UNION ALL match (c)-[:Remisor]->(r:Mensajes)-[:Rreceptor]->(:Usuario {idUsuario:$idUsuario}) return r} detach delete r"//delete mensajes GetChatsIndividuales
		//borra mensajes enviados por el usuario al grupo
		val script3 = s"match ((a:Usuario {idUsuario:$idUsuario})-[:Remisor]->(r:Mensajes)-[:RreceptorGrupo]->(b:Grupo)) detach delete r"
		//grupos del usuario
		val script4 = s"Match (b)-[:Rgrupo]->(a:Usuario {idUsuario:$idUsuario}) return b.idGrupo AS idGrupo"
		val result = session.run(script)
		val result1 = session.run(script2)
		val result2 = session.run(script3)
		val result3 = session.run(script4)
		val record_fetch = if (result3.hasNext()) {
	    	while(result3.hasNext()){
   				var record = result3.next()
   				SacarUsuarioGrupo(idUsuario,record.get("idGrupo").asInt())
			}  		
	    }
	    val result4 = session.run(script1)
		session.close()
		result4.consume().counters().nodesDeleted()
	}
	def UpdateUsuario(usuario:UsuarioUpdateModel):String={
		var jsonUsuario=s"{Apodo:'${usuario.Apodo}',Nombre: '${usuario.Nombre}',Apellido:'${usuario.Apellido}',FotoPerfil:'${usuario.FotoPerfil}',Estado:'${usuario.Estado}'}"
		val script = s"match(n:Usuario {idUsuario:${usuario.idUsuario}}) set n+=${jsonUsuario} return n.idUsuario AS idUsuario,n.Apodo As Apodo, n.Nombre AS Nombre, n.Apellido AS Apellido,n.FotoPerfil AS FotoPerfil,n.Estado AS Estado"
		val session = Neo4j.driver.session
		val result=session.run(script)
		val record_data = if (result.hasNext()) {
			val record = result.next()
			s"""{"idUsuario":${record.get("idUsuario").asInt()},"Nombre":"${record.get("Nombre").asString()}","Apellido":"${record.get("Apellido").asString()}","Estado":"${record.get("Estado").asString()}","FotoPerfil":"${record.get("FotoPerfil").asString()}","Apodo":"${record.get("Apodo").asString()}"}"""
	    }else{
	      s"${usuario.idUsuario} not modified."
	    }
		session.close()
		record_data
	}

	def SacarUsuarioGrupo(idUsuario1:Int,idGrupo:Int):Int={
		//esta en el grupo
		val script=s"match (a:Grupo {idGrupo:$idGrupo}),(b:Usuario {idUsuario:$idUsuario1}) return exists((a)-[:Rgrupo]->(b)) as result, a.idCreador=$idUsuario1 as result1"
		//borra mensajes enviados por el usuario al grupo
		val script1 = s"match ((a:Usuario {idUsuario:$idUsuario1})-[:Remisor]->(r:Mensajes)-[:RreceptorGrupo]->(b:Grupo {idGrupo:$idGrupo})) detach delete r"
		//miembros del grupo igual a uno
		val script2= s"match ((b:Grupo {idGrupo:$idGrupo})-[:Rgrupo]->(a:Usuario)) return count(a)=1 AS result"
		//borra grupo
		val script3=s"match (b:Grupo {idGrupo:$idGrupo}) detach delete b"
		//borra relacion de usuario y grupo
		val script4= s"match ((b:Grupo {idGrupo:$idGrupo})-[r:Rgrupo]->(a:Usuario {idUsuario:$idUsuario1})) delete r "
		//set nuevo admin
		val script5= s"call{match ((b:Grupo {idGrupo:$idGrupo})-[r:Rgrupo]->(a:Usuario )) return a.idUsuario as result limit 1}match (b:Grupo {idGrupo:$idGrupo}) set b.idCreador=result"
		val session = Neo4j.driver.session
		val result = session.run(script)
		val finalResult =if (result.hasNext()) {
			val record = result.next()
			//esta en grupo
			if(record.get("result").asBoolean()){
				//solo en grupo?
				val result1 = session.run(script1)//borra mensajes
				val result3 = session.run(script2)
				val result2 = session.run(script4)//borra relaciones entre el grupo y usuario
				if(result3.hasNext()){
					val record1=result3.next()
					if(!(record1.get("result").asBoolean())){
						//no esta solo
						if(record.get("result1").asBoolean()){
							//no esta solo 
							val result4 = session.run(script5)
						}
					}else{
						val result6 = session.run(script3)
					}
				}
				result2.consume().counters().relationshipsDeleted()
			}else{
				0
			}
	    }else{
	      0
	    }
		session.close()
		finalResult
	}
	//Seguidores
	def CreateSeguir(createSeguir : CreateSeguir):Int={
		val session = Neo4j.driver.session
		val script1=s"match (a:Usuario),(b:Usuario) where a.idUsuario=${createSeguir.idUsuario2} AND b.idUsuario=${createSeguir.idUsuario1} return distinct exists((a)-[:Rbloqueado]->(b)) as result"
		val script2 = s"match (a:Usuario),(b:Usuario) where a.idUsuario=${createSeguir.idUsuario1} AND b.idUsuario=${createSeguir.idUsuario2} AND Not(Exists((a)-[:Rsigue]->(b))) create (a)-[:Rsigue]->(b)"
		val result = session.run(script1)
		val finalResult =if (result.hasNext()) {
			val record = result.next()
			if(record.get("result").asBoolean()){
				0
			}else{
				val result1 = session.run(script2)
				result1.consume().counters().relationshipsCreated()
			}
			
	    }else{
	      0
	    }
		session.close()
		finalResult
	}
	def GetASigueB(u1:Int,u2:Int):Boolean={
		val session = Neo4j.driver.session
		val script = s"match (a:Usuario),(b:Usuario) where a.idUsuario=$u1 AND b.idUsuario=$u2 return distinct exists((a)-[:Rsigue]->(b)) AS result  "
		val result=session.run(script)
		val record_data = if (result.hasNext()) {
			val record = result.next()
			record.get("result").asBoolean()
	    }else{
	    	false
	    }
		session.close()
		return record_data
	}
	def GetSeguidos(idUsuario:Int):String={
		val session = Neo4j.driver.session
		val script = s"match(a:Usuario {idUsuario:$idUsuario})-[:Rsigue]->(b:Usuario) return b.idUsuario AS idUsuario,b.Apodo AS Apodo"
		val result =session.run(script)
		var jsonArray=""
	    val record_fetch = if (result.hasNext()) {
	    	while(result.hasNext()){
   				var record = result.next()
   				jsonArray+=s"""{"idUsuario":${record.get("idUsuario").asInt()},"Apodo":"${record.get("Apodo").asString()}"}"""
				if(result.hasNext()){
					jsonArray+=","
				}
			}
			jsonArray = "[ "+jsonArray+" ]"
			jsonArray   		
	    }else{
	    	s"usuarios seguidos de $idUsuario no encontrados "
	    }
	    session.close()
	    record_fetch
	}
	def GetSeguidores(idUsuario:Int):String={
		val session = Neo4j.driver.session
		val script = s"match(a:Usuario )-[:Rsigue]->(b:Usuario {idUsuario:$idUsuario}) return a.idUsuario AS idUsuario,a.Apodo AS Apodo"
		val result =session.run(script)
		var jsonArray=""
	    val record_fetch = if (result.hasNext()) {
	    	while(result.hasNext()){
   				var record = result.next()
   				jsonArray+=s"""{"idUsuario":${record.get("idUsuario").asInt()},"Apodo":"${record.get("Apodo").asString()}"}"""
				if(result.hasNext()){
					jsonArray+=","
				}
			}
			jsonArray = "[ "+jsonArray+" ]"
			jsonArray   		
	    }else{
	    	s"usuarios seguidos de $idUsuario no encontrados "
	    }
	    session.close()
	    record_fetch
	}
	def DeleteSeguir(deleteSeguir : DeleteSeguir):Int={
		val session = Neo4j.driver.session
		val script = s"MATCH (a:Usuario {idUsuario:${deleteSeguir.idUsuario1}})-[r:Rsigue]->(b:Usuario {idUsuario:${deleteSeguir.idUsuario2}}) delete r"
		val result = session.run(script)
		session.close()
		result.consume().counters().nodesDeleted()
	}

	//Bloquear
	def CreateBloquear(createBloquear : CreateBloquear):Int={
		if(GetASigueB(createBloquear.idUsuario1,createBloquear.idUsuario2)){
			val deleteSeguir = new DeleteSeguir(createBloquear.idUsuario1,createBloquear.idUsuario2)
			val delete=DeleteSeguir(deleteSeguir)
		}
		val session = Neo4j.driver.session
		val script = s"match (a:Usuario),(b:Usuario) where a.idUsuario=${createBloquear.idUsuario1} AND b.idUsuario=${createBloquear.idUsuario2} AND Not(Exists((a)-[:Rbloqueado]->(b))) create (a)-[:Rbloqueado]->(b)"
		val result = session.run(script)
		session.close()
		result.consume().counters().relationshipsCreated()
	}
	def GetABloqueaB(u1:Int,u2:Int):Boolean={
		val session = Neo4j.driver.session
		val script = s"match (a:Usuario),(b:Usuario) where a.idUsuario=$u1 AND b.idUsuario=$u2 return distinct exists((a)-[:Rbloqueado]->(b)) AS result  "
		val result=session.run(script)
		val record_data = if (result.hasNext()) {
			val record = result.next()
			record.get("result").asBoolean()
	    }else{
	    	false
	    }
		session.close()
		return record_data
	}
	def GetBloqueados(idUsuario:Int):String={
		val session = Neo4j.driver.session
		val script = s"match(a:Usuario {idUsuario:$idUsuario})-[:Rbloqueado]->(b:Usuario) return b.idUsuario AS idUsuario,b.Apodo AS Apodo"
		val result =session.run(script)
		var jsonArray=""
	    val record_fetch = if (result.hasNext()) {
	    	while(result.hasNext()){
   				var record = result.next()
   				jsonArray+=s"""{"idUsuario":${record.get("idUsuario").asInt()},"Apodo":"${record.get("Apodo").asString()}"}"""
				if(result.hasNext()){
					jsonArray+=";"
				}
			}
			jsonArray   		
	    }else{
	    	s"usuarios bloqueados de $idUsuario no encontrados "
	    }
	    session.close()
	    record_fetch
	}
	def DeleteBloquear(deleteBloquear : DeleteBloquear):Int={
		val session = Neo4j.driver.session
		val script = s"MATCH (a:Usuario {idUsuario:${deleteBloquear.idUsuario1}})-[r:Rbloqueado]->(b:Usuario {idUsuario:${deleteBloquear.idUsuario2}}) delete r"
		val result = session.run(script)
		session.close()
		result.consume().counters().relationshipsDeleted()
	}
}
