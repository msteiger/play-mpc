import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

    val appName         = "play-mpc"
    val appVersion      = "2.2-SNAPSHOT"

    val appDependencies = Seq(
      javaCore,
      javaJdbc,
      javaEbean,
      "com.google.guava" % "guava" % "14.0.1"
    )

    val main = play.Project(appName, appVersion, appDependencies).settings(
      // Add your own project settings here      
    )

}
            
