# AUGMENT-SBT

augment-sbt is a very opinionated set of SBT plugins that have been in use and maintained by TrafficLand, Inc. 
since July, 2013. They have been modified as our needs have changed and our processes have become more stable and mature. 

We're not saying these plugins represent processes everyone should use 
but you should, at least, take a look at them. At the very least you will learn how to write your own plugins and can
use this project's code as a guide and a good foundation.

## General Usage

augment-sbt targets sbt 0.13.6+.

You will need to add the following to your `project/build.properties` 
file if you have multiple versions of sbt installed

    sbt.version=0.13.x // where x is greater or equal to 6

Be sure to use the [latest launcher](http://www.scala-sbt.org/download.html).

### Getting AUGMENT-SBT.

Using sbt 0.13.6+, add the following to your `project/plugins.sbt`

```scala
resolvers += Resolver.url("bintray-trafficland-sbt-plugins", url("https://dl.bintray.com/trafficland/sbt-plugins/"))(
 Patterns(isMavenCompatible = false, Resolver.localBasePattern)
)
addSbtPlugin("com.trafficland" % "augmentsbt" % "1.0.0")
```

This will add JCenter (the [analog to maven central for bintray](https://bintray.com/bintray/jcenter)) 
to your resolver chain as well as download and install augment-sbt. 

### Using AUGMENT-SBT

augment-sbt is self referential so the best place to see a real world example of the plugins in use is the `build.sbt`
in the root of the project.

To get up and running quickly you can enable the 
[StandardPluginSet](https://github.com/ereichert/augment-sbt/blob/master/src/main/scala/com/trafficland/augmentsbt/StandardPluginSet.scala).
Since the augment-sbt plugins are 
[AutoPlugins](http://www.scala-sbt.org/release/docs/Plugins.html) typically you would enable the `StandardPluginSet`
by adding the following project definition to a `build.sbt` file in your project.
    
    lazy val project = Project("projectName", file("."))
      .enablePlugins(StandardPluginSet)
      .settings(
        .
        .
        .
      )

If you want to customize the plugins you use in your project you can add individual enablePlugins to your project
definition.

## The Plugins

##### AppInfoPlugin

Generates an AppInfo class (in AppInfo.scala) having information about the application which can be used in the 
program at runtime.
 
##### BuildInfoPlugin

Generates an BuildInfo class (in BuildInfo.scala) having information about the application's build which can 
be used in the program at runtime.

##### CentOSRPMPlugin

Creates RPM targeting CentOS using the sbt-native-packager plugin underneath.

##### CentOSPlayRPMPlugin

Creates RPM targeting CentOS for Play projects using the sbt-native-packager plugin underneath.

##### GeneratorsPlugin

A convenience plugin for adding several of the generator plugins: AppInfoPlugin, BuildInfoPlugin, 
LogbackConfigurationPlugin

##### GitPlugin

Used for many of the release tasks but can also provide access to common git tasks from the SBT CLI.

##### LogbackConfigurationPlugin

Generates an logback.xml and logback-test.xml files.

##### PackageManagementPlugin

Modifies the names of the artifacts produced by SBT builds.

##### Play20Plugin

Modifies some of the default settings in the official Play plugin.

##### ReleaseManagementPlugin

Handles snapshot (releaseSnapshot) and final (releaseFinal *version*) releases with a single command at the SBT CLI. 
The tasks run by the release commands and the order in which they are run can be read at 
[releaseSnapshot](https://github.com/ereichert/augment-sbt/blob/master/src/main/scala/com/trafficland/augmentsbt/releasemanagement/SnapshotReleaseTasks.scala)
and 
[releaseFinal](https://github.com/ereichert/augment-sbt/blob/master/src/main/scala/com/trafficland/augmentsbt/releasemanagement/FinalReleaseTasks.scala).

##### RPMPlugin

Creates RPM using the sbt-native-packager plugin underneath.

##### ScalaConfigurationPlugin

Adds typical (according to us) Scala compiler settings to your project.
 
##### StandardPluginSet

A convenience plugin for adding several oft used plugins to your project: 
PackageManagementPlugin, ReleaseManagementPlugin, ScalaConfigurationPlugin, GeneratorsPlugin

##### StartupScriptPlugin

Works with sbt-native-packager to create a customized start script.

##### VersionManagementPlugin

Adds the ability to modify the semantic versioning of a project from the SBT CLI.

## Thanks

The original version was largely based on the work done by Robey Pointer which can be found at 
https://github.com/twitter/sbt-package-dist.