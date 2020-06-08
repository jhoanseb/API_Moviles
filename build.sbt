name := """BB"""
organization := "com.example"

version := "1.0-SNAPSHOT"
lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.2"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test
libraryDependencies += "org.neo4j.driver" % "neo4j-java-driver" % "4.0.1"
libraryDependencies += "org.json4s" %% "json4s-native" % "3.7.0-M4"

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.example.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"
