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

object MensajesRepo extends App {

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
	def CreateMensaje(createMensajeModel : CreateMensajeModel):Int={
		if(!(GetABloqueaB(createMensajeModel.idUsuario2,createMensajeModel.idUsuario1))){
			val session = Neo4j.driver.session
			val script = s"call{match(n:Mensajes) return case when max(n.idMensaje) is null then 1 else max(n.idMensaje)+1 end as result} create(:Mensajes {idMensaje:result,Cuerpo:'${createMensajeModel.Cuerpo}',Tiempo:datetime()}) return result"		
			val result = session.run(script)
			val finalResult = if (result.hasNext()) {
				val record = result.next()
				val script1 = s"match (a:Mensajes {idMensaje:${record.get("result").asInt()}}),(b:Usuario {idUsuario:${createMensajeModel.idUsuario1}}),(c:Usuario {idUsuario:${createMensajeModel.idUsuario2}}) create (b)-[:Remisor]->(a)-[:Rreceptor]->(c)"	
				val result1 = session.run(script1)
				result1.consume().counters().relationshipsCreated()
		    }else{
		      0
		    }
			session.close()
			finalResult	
		}else{
			-1
		}	
	}
	def GetMensajes(idUsuario1:Int,idUsuario2:Int): String = {
		val session = Neo4j.driver.session
		val script = s"call{match (a:Usuario {idUsuario:$idUsuario1})-[:Remisor]->(c:Mensajes)-[:Rreceptor]->(b:Usuario {idUsuario:$idUsuario2}) return a.idUsuario AS idUsuario1,a.Apodo AS Apodo1,c.idMensaje AS idMensaje,c.Cuerpo AS Cuerpo,c.Tiempo AS Tiempo,b.idUsuario AS idUsuario2,b.Apodo AS Apodo2 UNION ALL match (a:Usuario {idUsuario:$idUsuario2})-[:Remisor]->(c:Mensajes)-[:Rreceptor]->(b:Usuario {idUsuario:$idUsuario1}) return a.idUsuario AS idUsuario1,a.Apodo AS Apodo1,c.idMensaje AS idMensaje,c.Cuerpo AS Cuerpo,c.Tiempo AS Tiempo,b.idUsuario AS idUsuario2,b.Apodo AS Apodo2} return idUsuario1,Apodo1,idMensaje,Cuerpo,Tiempo,idUsuario2,Apodo2 order by Tiempo"
		val result =session.run(script)
		var jsonArray=""
	    val record_fetch = if (result.hasNext()) {
	    	while(result.hasNext()){
   				var record = result.next()
   				jsonArray+=s"""{"idUsuario1":${record.get("idUsuario1").asInt()},"Apodo1":"${record.get("Apodo1").asString()}","idMensaje":${record.get("idMensaje").asInt()},"Cuerpo":"${record.get("Cuerpo").asString()}","Tiempo":"${record.get("Tiempo")}","idUsuario2":${record.get("idUsuario2").asInt()},"Apodo2":"${record.get("Apodo2").asString()}"}"""
				if(result.hasNext()){
					jsonArray+=","
				}
			}
            jsonArray = "[ "+jsonArray+" ]"
			jsonArray   		
	    }else{
	    	s"Mensajes entre $idUsuario1 y $idUsuario2 no encontrados "
	    }
	    session.close()
	    record_fetch
	}
	def GetChatsIndividuales(id:Int=0):String={
		val session = Neo4j.driver.session
		val script = s"call{match (:Usuario {idUsuario:$id})-[:Remisor]->(r:Mensajes)-[:Rreceptor]->(c:Usuario) return c.idUsuario AS idUsuario,c.Apodo AS Apodo order by r.Tiempo desc UNION ALL match (c:Usuario)-[:Remisor]->(r:Mensajes)-[:Rreceptor]->(:Usuario {idUsuario:$id}) return c.idUsuario AS idUsuario,c.Apodo AS Apodo order by r.Tiempo desc} return distinct idUsuario,Apodo"
		val result=session.run(script)
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
	    	s"chats de $id no encontrados"
	    }
	    session.close()
	    record_fetch
	}
}