package services.neo

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;
import org.neo4j.driver.Result;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.TransactionWork;
import services.globals.Config

object Neo4j extends App {
    def driver = GraphDatabase.driver(Config.route, AuthTokens.basic(Config.user, Config.password))
}
