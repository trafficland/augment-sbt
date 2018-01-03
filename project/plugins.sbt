addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.3.2")
addSbtPlugin("org.foundweekends" % "sbt-bintray" % "0.5.2")

libraryDependencies ++= Seq(
  "org.eclipse.jgit" % "org.eclipse.jgit" % "4.5.0.201609210915-r",
  "org.eclipse.jgit" % "org.eclipse.jgit.pgm" % "4.5.0.201609210915-r" exclude("javax.jms", "jms")
                                                                       exclude("com.sun.jdmk", "jmxtools")
                                                                       exclude("com.sun.jmx", "jmxri"),
  "com.jcraft" % "jsch.agentproxy.sshagent" % "0.0.9",
  "com.jcraft" % "jsch.agentproxy.usocket-jna" % "0.0.9"
)