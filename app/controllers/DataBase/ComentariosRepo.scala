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

object ComentariosRepo extends App {
	def CreateComentario(createComentarioModel : CreateComentarioModel):Int={
		val session = Neo4j.driver.session
		val script = s"call{match()-[r:Rcomenta]->() return case when max(r.idComentario) is null then 1 else max(r.idComentario)+1 end as result} match (b:Publicaciones {idPublicacion:${createComentarioModel.idPublicacion}}),(a:Usuario {idUsuario:${createComentarioModel.idUsuario}}) create (a)-[:Rcomenta {idComentario:result,Cuerpo:'${createComentarioModel.Cuerpo}',Tiempo:datetime()}]->(b)"
		val result = session.run(script)
		session.close()
		result.consume().counters().relationshipsCreated()
	}
	def GetComentariosDePublicacion(idPublicacion:Int): String = {
		val session = Neo4j.driver.session
		val script = s"match(a)-[r:Rcomenta]->(b:Publicaciones {idPublicacion:$idPublicacion}) return a.idUsuario as idUsuario,a.Apodo AS Apodo,r.idComentario AS idComentario, r.Cuerpo AS Cuerpo,r.Tiempo AS Tiempo ORDER BY r.Tiempo"
		val result =session.run(script)
		var jsonArray=""
	    val record_fetch = if (result.hasNext()) {
	    	while(result.hasNext()){
   				var record = result.next()
   				jsonArray+=s"""{"idUsuario":${record.get("idUsuario").asInt()},"Apodo":"${record.get("Apodo").asString()}","idComentario":${record.get("idComentario").asInt()},"Cuerpo":"${record.get("Cuerpo").asString()}","Tiempo":"${record.get("Tiempo")}"}"""
				if(result.hasNext()){
					jsonArray+=","
				}
			}
            jsonArray = "[ "+jsonArray+" ]"
			jsonArray   		
	    }else{
	    	s"comentarios de $idPublicacion no encontrados "
	    }
	    session.close()
	    record_fetch
	}
	def DeleteComentario(deleteComentario : DeleteComentario): Int={
		val session = Neo4j.driver.session
		val script = s"MATCH (a:Usuario {idUsuario:${deleteComentario.idUsuario})-[r:Rcomenta {idComentario:${deleteComentario.idComentario}}]->(b:Publicaciones {idPublicacion:${deleteComentario.idPublicacion}}) delete r"
		val result = session.run(script)
		session.close()
		result.consume().counters().relationshipsDeleted()
	}

}