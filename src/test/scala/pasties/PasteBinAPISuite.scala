package pasties

import scala.util.{Try, Success, Failure}
import dispatch._, Defaults._

import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

import org.junit.runner.RunWith

/**
  * This class is a test suite for the PasteBinAPI class and its methods.
  *
  * NOTE: In order to run these test successfully you must supply an API key
  *       which can be obtained just by creating an account with pastebin.
  *       You also must supply a username and password further below, which is
  *       commented accordingly.  These are mainly here just for completeness;
  *       individual testing should also employeed and these tests should not
  *       be relied on completely.
  *
  */
@RunWith(classOf[JUnitRunner])
class PasteBinSuite extends FunSuite {

  trait PasteBinAPISuite extends FunSuite {

    val devKey          = "Supply your developer key"
    val codeToPaste     = """
                            /**
                              * This is a simple factorial function in scala
                              *
                              */
                            def fact(n: Int): Int = if (n == 0) 1 else n * fact(n-1)
                          """.stripMargin

    def checkResult(result: Try[String], stringToCheck: String): Boolean = result match {

      case Success(value) if value.contains(stringToCheck) => true

      case Failure(err) => false

      case Success(badResponse) => false
    }

    def extractResult(result: Try[String]): String = result match {

      case Success(value) => value

      case Failure(err) => "Failed"
    }
  }

  test("Tests creation of paste, deletion of paste, creation of user key, and raw ouput") {

    /**
      * NOTE: These tests are very minimal
      *       due to there being more fluctuation
      *       in the potential responses received
      *       when executing the methods which are
      *       not tested here. Also, if you do not
      *       wish to create a user key, testing
      *       should be carried out individually or
      *       these tests should be altered.
      *
      */
    new PasteBinAPISuite {

      val APIInterface        = PasteBinAPI(devKey)

      val makePasteResponse   = APIInterface.makePaste(codeToPaste, nameOfPaste = "This is a test case!", expireDate = "10M",
                                    syntaxHighlight = "scala", privateLevel = "1")

      // In order to test the delete paste method, you must create a user key
      val user_key            = APIInterface.createAPIUserKey(_, _) // Enter your: username, password

      // This test requires a user key
      val makePasteAsUser     = APIInterface.makePaste(codeToPaste, userKey = extractResult(user_key), nameOfPaste = "This is a user test case!",
                                    expireDate = "10M", syntaxHighlight = "scala", privateLevel = "1")


      val deletePasteResponse = APIInterface.deletePaste(extractResult(user_key), extractResult(makePasteAsUser).split('/').last)

      val rawOutput           = getRawOutput(extractResult(makePasteResponse).split('/').last)

      // Test results
      assert(checkResult(makePasteResponse, "pastebin") === true)
      assert(checkResult(makePasteAsUser, "pastebin") === true)
      assert(checkResult(deletePasteResponse, "Paste Removed") === true)
      assert(checkResult(rawOutput, codeToPaste) === true)
    }
  }
}
