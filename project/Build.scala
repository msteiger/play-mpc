import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

    val appName         = "computer-database"
    val appVersion      = "1.0"

    val appDependencies = Seq(
      javaCore,
      javaJdbc,
      javaEbean
//      "log4j" % "log4j" % "1.2.16"
    )

    val main = play.Project(appName, appVersion, appDependencies).settings(
      // Add your own project settings here      
    )

}
            
