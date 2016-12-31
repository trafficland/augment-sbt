addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.1.4")

libraryDependencies ++= Seq(
  "org.eclipse.jgit" % "org.eclipse.jgit" % "4.5.0.201609210915-r",
  "com.jcraft" % "jsch.agentproxy.sshagent" % "0.0.9",
  "com.jcraft" % "jsch.agentproxy.usocket-jna" % "0.0.9"
)