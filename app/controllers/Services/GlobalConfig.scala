package services.globals

object Config{
    //DataBase
    lazy val route = "bolt://localhost:7687"
    lazy val user = "neo4j"
    lazy val password = "ne4j"

    //publicaciones
    lazy val page = 10
}