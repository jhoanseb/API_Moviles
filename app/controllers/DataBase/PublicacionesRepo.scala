package neo4j

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;
import org.neo4j.driver.Result;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.TransactionWork;
import services.globals.Config
import models._
import services.neo._

object PublicacionesRepo extends App {
    
	def GetPublicacionesDeSeguidos(idUsuario:Int=0):String={
		var limit = Config.page
		val session = Neo4j.driver.session
		val script = s"call{match(a:Usuario {idUsuario:$idUsuario})-[:Rsigue]->(b:Usuario)-[:Rpublica]->(c:Publicaciones) where Not(Exists((a)-[:Rvisto]->(c))) return  a,b,c order by c.Tiempo desc limit $limit}  create ((a)-[:Rvisto]->(c)) return distinct b.idUsuario AS idUsuario,b.Apodo AS Apodo,c.idPublicacion AS idPublicacion,c.Archivo AS Archivo,c.Tipo AS Tipo,c.Descripcion AS Descripcion,c.Tiempo AS Tiempo"
		val result=session.run(script)
		var jsonArray=""
	    val record_fetch = if (result.hasNext()) {
	    	while(result.hasNext()){
   				var record = result.next()
   				jsonArray+=s"""{"idUsuario":${record.get("idUsuario").asInt()},"Apodo":"${record.get("Apodo").asString()}","idPublicacion":${record.get("idPublicacion").asInt()},"Archivo":"${record.get("Archivo").asString()}","Tipo":"${record.get("Tipo").asString()}","Descripcion":"${record.get("Descripcion").asString()}","Tiempo":"${record.get("Tiempo")}"}"""
				if(result.hasNext()){
					jsonArray+=","
				}
			}
            jsonArray = "[ "+jsonArray+" ]"
			jsonArray   		
	    }else{
	    	"publicaciones de $idPublicacion no encontrados "
	    }
	    session.close()
	    record_fetch
	}
	def CreatePublicacion(publicacion : PublicacionModel):Int={
		val session = Neo4j.driver.session
		val script = s"call{match(n:Publicaciones) return case when max(n.idPublicacion) is null then 1 else max(n.idPublicacion)+1 end as result} create(:Publicaciones {idPublicacion:result,Tipo:'${publicacion.Tipo}',Archivo:'${publicacion.Archivo}',Descripcion:'${publicacion.Descripcion}',Tiempo:datetime()}) return result"		
		val result = session.run(script)
		val finalResult = if (result.hasNext()) {
			val record = result.next()
			val script1 = s"match (a:Publicaciones {idPublicacion:${record.get("result").asInt()}}),(b:Usuario {idUsuario:${publicacion.idUsuario}}) where Not(Exists((b)-[:Rpublica]->(a))) create (b)-[:Rpublica]->(a)"		
			val result1 = session.run(script1)
			result1.consume().counters().relationshipsCreated()
			
	    }else{
	      0
	    }
		session.close()
		finalResult		
	}
	def DeletePublicacion(deletePublicacion : DeletePublicacion):Int={
		val session = Neo4j.driver.session
		val script = s"MATCH (a:Usuario {idUsuario:${deletePublicacion.idUsuario}})-[:Rpublica ]->(b:Publicaciones {idPublicacion:${deletePublicacion.idPublicacion}}) detach delete b"
		val result = session.run(script)
		session.close()
		result.consume().counters().relationshipsDeleted()
	}
	def GetPublicacionesDeUsuario(idUsuario:Int=0):String={
		val session = Neo4j.driver.session
		val script = s"match (b:Usuario {idUsuario:$idUsuario})-[:Rpublica]->(a:Publicaciones) return distinct a.idPublicacion AS idPublicacion,a.Archivo AS Archivo,a.Tipo AS Tipo,a.Descripcion AS Descripcion,a.Tiempo AS Tiempo"
		val result=session.run(script)
		var jsonArray=""
	    val record_fetch = if (result.hasNext()) {
	    	while(result.hasNext()){
   				var record = result.next()
   				jsonArray+=s"""{"idPublicacion":${record.get("idPublicacion").asInt()},"Archivo":"${record.get("Archivo").asString()}","Tipo":"${record.get("Tipo").asString()}","Descripcion":"${record.get("Descripcion").asString()}","Tiempo":"${record.get("Tiempo")}"}"""
				if(result.hasNext()){
					jsonArray+=","
				}
			}
			jsonArray = "[ "+jsonArray+" ]"
            jsonArray	
	    }else{
	    	s"publicaciones de ${idUsuario} no encontrados "
	    }
	    session.close()
	    record_fetch
	}
    //Likes
    def CreateLike(createLike : CreateLike):Int={
		val session = Neo4j.driver.session
		val script = s"match (a:Usuario {idUsuario:${createLike.idUsuario}}),(b:Publicaciones {idPublicacion:${createLike.idPublicacion}}) where Not(Exists((a)-[:Rlike]->(b))) create (a)-[:Rlike]->(b)"
		val result = session.run(script)
		session.close()
		result.consume().counters().relationshipsCreated()
	}
	def GetLikesDePublicacion(id:Int=0):String={
		val session = Neo4j.driver.session
		val script = s"match ((a)-[r:Rlike]->(b:Publicaciones {idPublicacion:$id})) return distinct a.idUsuario AS idUsuario,a.Apodo AS Apodo"
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
	    	s"likes de $id no encontrados"
	    }
	    session.close()
	    record_fetch
	}
	def DeleteLike(deleteLike : DeleteLike):Int={
		val session = Neo4j.driver.session
		val script = s"MATCH (b:Usuario {idUsuario:${deleteLike.idUsuario}})-[r:Rlike]->(a:Publicaciones {idPublicacion:${deleteLike.idPublicacion}}) delete r"
		val result = session.run(script)
		session.close()
		result.consume().counters().relationshipsDeleted()
	}

    //Etiquetas
	def CreateEtiqueta(createEtiqueta : CreateEtiqueta):Int={
		val session = Neo4j.driver.session
		val script = s"match (a:Usuario {idUsuario:${createEtiqueta.idUsuario}}),(b:Publicaciones {idPublicacion:${createEtiqueta.idPublicacion}}) where Not(Exists((b)-[:Retiqueta]->(a))) create (b)-[:Retiqueta]->(a)"
		val result = session.run(script)
		session.close()
		result.consume().counters().relationshipsCreated()
	}
	def GetEtiquetasDePublicacion(idPublicacion:Int):String={
		val session = Neo4j.driver.session
		val script = s"MATCH (a:Publicaciones {idPublicacion:$idPublicacion})-[r:Retiqueta]->(b:Usuario) return b.idUsuario AS idUsuario,b.Apodo AS Apodo"
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
	    	s"Etiquetas de $idPublicacion no encontradas "
	    }
	    session.close()
	    record_fetch
	}
	def DeleteEtiqueta(deleteEtiqueta : DeleteEtiqueta):Int={
		val session = Neo4j.driver.session
		val script = s"MATCH (a:Publicaciones {idPublicacion:${deleteEtiqueta.idPublicacion}})-[r:Retiqueta]->(b:Usuario {idUsuario:${deleteEtiqueta.idUsuario}}) delete r"
		val result = session.run(script)
		session.close()
		result.consume().counters().relationshipsDeleted()
	}
}