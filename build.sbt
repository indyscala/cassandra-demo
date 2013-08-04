scalaVersion := "2.10.2"

libraryDependencies ++= Seq(
  "commons-io" % "commons-io" % "2.4",
  "com.cybozu.labs" % "langdetect" % "1.1-20120112",
  "com.datastax.cassandra" % "cassandra-driver-core" % "1.0.2-dse2",
  "com.typesafe" %% "scalalogging-slf4j" % "1.0.1",
  "com.typesafe.akka" %% "akka-actor" % "2.2.0",
  "net.databinder.dispatch" %% "dispatch-core" % "0.11.0",
  "org.eclipse.jetty" % "jetty-webapp" % "8.1.8.v20121106",
  "org.eclipse.jetty.orbit" % "javax.servlet" % "3.0.0.v201112011016" artifacts (Artifact("javax.servlet", "jar", "jar")),
  "org.json4s" %% "json4s-jackson" % "3.2.5",
  "org.scalatra" %% "scalatra" % "2.2.1",
  "org.scalatra" %% "scalatra-json" % "2.2.1"
)

ivyXML := <dependencies>
  <exclude module="servlet-api"/>
</dependencies>