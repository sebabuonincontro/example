name := "example"

version := "1.0"

scalaVersion := "2.11.8"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= {
  val sprayVersion = "1.3.2"
  Seq (
    "com.typesafe.akka"           %% "akka-actor"          % "2.4.4"
    // -- Spray --
    ,"io.spray"                   %% "spray-can"           % sprayVersion
    ,"io.spray"                   %% "spray-routing"       % sprayVersion
    ,"io.spray"                   %% "spray-json"          % sprayVersion
    ,"io.spray"                   %% "spray-http"          % sprayVersion
    // -- Logging --
    ,"ch.qos.logback"              % "logback-classic"     % "1.1.3"
    ,"com.typesafe.scala-logging" %% "scala-logging"       % "3.1.0"
    ,"com.typesafe.akka"          %% "akka-testkit"        % "2.4.4" % "test"
    // -- time --
    ,"joda-time"                   % "joda-time"           % "2.7"
    // -- config --
    ,"com.typesafe"                % "config"              % "1.2.1"
    // -- database --
    ,"com.typesafe.slick"         %% "slick"               % "3.1.1"
    //H2 provider
    //,"com.h2database"             % "h2"                   % "1.3.170"
    //HsqlDB Driver
    ,"org.hsqldb"                 % "hsqldb"               % "2.3.1"
    //json
    ,"org.json4s"                 %% "json4s-native" % "3.2.11"
    ,"org.json4s"                 %% "json4s-ext" % "3.2.11"
    , "org.json4s"                %% "json4s-jackson" % "3.2.11"
)}
