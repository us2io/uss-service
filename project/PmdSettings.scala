import sbt._
import Keys._

//Adapted from https://github.com/ymasory/sbt-code-quality.g8
object PmdSettings {

  case class PmdError(bugType: String, bugInfo: String, lineNumber: String, filename: String, priority: Int)

  val pmd = TaskKey[Unit]("pmd", "run pmd, placing results in target/pmd")
  val pmdTask = pmd <<=
    (streams, baseDirectory, sourceDirectory in Compile, target) map {
      (streams, base, src, target) =>
        import net.sourceforge.pmd.PMD.{ main => PmdMain }
        import streams.log
        val outputDir = (target / "pmd").mkdirs
        val outputFile = (target / "pmd" / "pmd-report.xml").getAbsolutePath

        val args = List(
          "-dir", src.getAbsolutePath,
          "-failOnViolation", "false",
          "-format", "xml",
          "-rulesets", (base / "project" / "pmd-ruleset.xml").getAbsolutePath,
          "-reportfile", outputFile
        )

        log info "Running PMD..."
        trappingExits {
          java.lang.Thread.currentThread.setContextClassLoader(this.getClass.getClassLoader)
          PmdMain(args.toArray)
        }

        val report = scala.xml.XML.loadFile(file(outputFile))

        val violations = (report \\ "file").flatMap(file => {
          (file \\ "violation").map(bug => {
            val bugType = bug.attribute("rule").get.head.text
            val bugInfo = bug.text.trim
            val lineNumber = bug.attribute("beginline").get.head.text
            val filename = file.attribute("name").get.head.text
            val priority = bug.attribute("priority").get.head.text
            PmdError(bugType, bugInfo, lineNumber, filename, priority.toInt)
          })
        }).sortBy(error => (error.priority, error.filename, error.lineNumber))

        val (errors, warnings) = violations.partition(_.priority <= 3)

        errors.foreach(error => {
          log error s"[${error.priority}] ${error.filename}:${error.lineNumber} ${error.bugType} - ${error.bugInfo}"
        })

        warnings.foreach(error => {
          log warn s"[${error.priority}] ${error.filename}:${error.lineNumber} ${error.bugType} - ${error.bugInfo}"
        })

        if (errors.nonEmpty) {
          log error (errors.length + " error(s) found in PMD report: " + outputFile + "")
          sys.exit(1)
        }

    }

  def trappingExits(thunk: => Unit): Unit = {
    val originalSecManager = System.getSecurityManager
    case class NoExitsException() extends SecurityException
    System setSecurityManager new SecurityManager() {
      import java.security.Permission
      override def checkPermission(perm: Permission) {
        if (perm.getName startsWith "exitVM") throw NoExitsException()
      }
    }
    try {
      thunk
    } catch {
      case _: NoExitsException =>
      case e : Throwable =>
    } finally {
      System setSecurityManager originalSecManager
    }
  }
}
