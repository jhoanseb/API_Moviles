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

object GruposRepo extends App {
    def CreateGrupo(createGrupoModel : CreateGrupoModel):Int={
		val session = Neo4j.driver.session
		val script = s"call{match(n:Grupo) return case when max(n.idGrupo) is null then 1 else max(n.idGrupo)+1 end as result} create(:Grupo {idGrupo:result,Tema:'${createGrupoModel.Tema}',Nombre:'${createGrupoModel.Nombre}'}) return result"		
		val result = session.run(script)
		val finalResult = if (result.hasNext()) {
			val record = result.next()
			var script1 = s"match (a:Usuario {idUsuario:${createGrupoModel.idUsuario}}),(b:Grupo {idGrupo:${record.get("result").asInt()}}) where Not(Exists((b)-[:Rgrupo]->(a))) create (b)-[:Rgrupo]->(a)"	
			var result1 = session.run(script1)
			result1.consume().counters().relationshipsCreated()
	    }else{
	      0
	    }
	    session.close()
		finalResult 
	}
	def AgregarUsuarioGrupo(addUsuarioGrupoModel : AddUsuarioGrupoModel):Int={
		val session = Neo4j.driver.session
		val script = s"match (a:Usuario {idUsuario:${addUsuarioGrupoModel.idUsuario}}),(b:Grupo {idGrupo:${addUsuarioGrupoModel.idGrupo}}) where Not(Exists((b)-[:Rgrupo]->(a))) create (b)-[:Rgrupo]->(a)"
		val result = session.run(script)
		session.close()
		result.consume().counters().relationshipsCreated()
	}
	def GetMensajesGrupo(idGrupo:Int): String = {
		val session = Neo4j.driver.session
		val script = s"match (b:Usuario)-[:Remisor]->(a:Mensajes)-[:RreceptorGrupo]->(c:Grupo {idGrupo:$idGrupo}) return b.idUsuario AS idUsuario,b.Apodo AS Apodo, a.idMensaje AS idMensaje,a.Cuerpo AS Cuerpo,a.Tiempo AS Tiempo order by Tiempo"
		val result =session.run(script)
		var jsonArray=""
	    val record_fetch = if (result.hasNext()) {
	    	while(result.hasNext()){
   				var record = result.next()
   				jsonArray+=s"""{"idUsuario":${record.get("idUsuario").asInt()},"Apodo":"${record.get("Apodo").asString()}","idMensaje":${record.get("idMensaje").asInt()},"Cuerpo":"${record.get("Cuerpo").asString()}","Tiempo":"${record.get("Tiempo")}"}"""
				if(result.hasNext()){
					jsonArray+=","
				}
			}
			jsonArray = "[ "+jsonArray+" ]"
			jsonArray   		
	    }else{
	    	s"Mensajes del grupo $idGrupo no encontrados "
	    }
	    session.close()
	    record_fetch
	}
	def GetGrupos(idUsuario:Int): String = {
		val session = Neo4j.driver.session
		val script = s"Match (b)-[:Rgrupo]->(a:Usuario {idUsuario:$idUsuario}) return b.idGrupo AS idGrupo,b.Tema AS Tema,b.Nombre AS Nombre"
		val result =session.run(script)
		var jsonArray=""
	    val record_fetch = if (result.hasNext()) {
	    	while(result.hasNext()){
   				var record = result.next()
   				jsonArray+=s"""{"idGrupo":${record.get("idGrupo").asInt()},"Tema":"${record.get("Tema").asString()}","Nombre":"${record.get("Nombre").asString()}"}"""
				if(result.hasNext()){
					jsonArray+=","
				}
			}
            jsonArray = "[ "+jsonArray+" ]"
			jsonArray   		
	    }else{
	    	s"grupos de $idUsuario no encontrados "
	    }
	    session.close()
	    record_fetch
	}
	def CreateMensajeGrupo(createMensajeGrupo : CreateMensajeGrupo):Int={
		val session =  Neo4j.driver.session
		val script = s"call{match(n:Mensajes) return case when max(n.idMensaje) is null then 1 else max(n.idMensaje)+1 end as result} create(:Mensajes {idMensaje:result,Cuerpo:'${createMensajeGrupo.Cuerpo}',Tiempo:datetime()}) return result"		
		val result = session.run(script)
		val finalResult = if (result.hasNext()) {
			val record = result.next()
			val script1 = s"match (a:Mensajes {idMensaje:${record.get("result").asInt()}}),(b:Usuario {idUsuario:${createMensajeGrupo.idUsuario}}),(c:Grupo {idGrupo:${createMensajeGrupo.idGrupo}}) create (b)-[:Remisor]->(a)-[:RreceptorGrupo]->(c)"	
			val result1 = session.run(script1)
			result1.consume().counters().relationshipsCreated()
	    }else{
	      0
	    }
		session.close()
		finalResult		
	}
	def SacarUsuarioGrupo(removeUsuarioGrupoModel : RemoveUsuarioGrupoModel):Int={
		//esta en el grupo
		val script=s"match (a:Grupo {idGrupo:${removeUsuarioGrupoModel.idGrupo}}),(b:Usuario {idUsuario:${removeUsuarioGrupoModel.idUsuario}}) return exists((a)-[:Rgrupo]->(b)) as result, a.idCreador=${removeUsuarioGrupoModel.idUsuario} as result1"
		//borra mensajes enviados por el usuario al grupo
		val script1 = s"match ((a:Usuario {idUsuario:${removeUsuarioGrupoModel.idUsuario}})-[:Remisor]->(r:Mensajes)-[:RreceptorGrupo]->(b:Grupo {idGrupo:${removeUsuarioGrupoModel.idGrupo}})) detach delete r"
		//miembros del grupo igual a uno
		val script2= s"match ((b:Grupo {idGrupo:${removeUsuarioGrupoModel.idGrupo}})-[:Rgrupo]->(a:Usuario)) return count(a)=1 AS result"
		//borra grupo
		val script3=s"match (b:Grupo {idGrupo:${removeUsuarioGrupoModel.idGrupo}}) detach delete b"
		//borra relacion de usuario y grupo
		val script4= s"match ((b:Grupo {idGrupo:${removeUsuarioGrupoModel.idGrupo}})-[r:Rgrupo]->(a:Usuario {idUsuario:${removeUsuarioGrupoModel.idUsuario}})) delete r "
		//set nuevo admin
		val script5= s"call{match ((b:Grupo {idGrupo:${removeUsuarioGrupoModel.idGrupo}})-[r:Rgrupo]->(a:Usuario )) return a.idUsuario as result limit 1}match (b:Grupo {idGrupo:${removeUsuarioGrupoModel.idGrupo}}) set b.idCreador=result"
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
}