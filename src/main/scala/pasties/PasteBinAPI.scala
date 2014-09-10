package pasties
/**
  * Provides a class as an interface to
  * the pastebin API.
  *
  * The only class to use is [[pasties.PasteBinAPI]].  Example usage:
  * {{{
  * scala> val APIInterface = PasteBinAPI("Your developer key")
  * }}}
  *
  * @author bmcg
  * @version 0.0.1
  *
  */

// Imports
import scala.util.{Try, Success, Failure}
import dispatch._, Defaults._

/**
  * __Author__  = bmcg
  * __Email__   = b.mcg0890@gmail.com
  * __VERSION__ = 0.0.1
  *
  * __License__ = 
  * Copyright (C) 2014-2016 b-mcg <b.mcg0890@gmail.com>
  * This program is free software: you can redistribute it and/or modify
  * it under the terms of the GNU General Public License as published by
  * the Free Software Foundation, either version 3 of the License, or
  * (at your option) any later version.
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
  * GNU General Public License for more details.
  * You should have received a copy of the GNU General Public License
  * along with this program. If not, see <http://www.gnu.org/licenses/>.
  *
  */

/**
  * Factory for [[pasties.PasteBinAPI]]
  *
  */
object PasteBinAPI {

  type MS = Map[String, String]
  type S  = String

  /**
    * Simple interface to the pastebin API.
    *
    * @note Requires a valid developer key
    *       which can be obtained by creating
    *       a free account with pastebin.
    *
    * @constructor Create an interface to the pastebin API
    * @param devKey: String: Valid developer key
    *
    */
  class PasteBinAPI(val devKey: S) {

    private val MAKE_PASTE_URL        = "http://pastebin.com/api/api_post.php"
    private val USER_KEY_URL          = "http://pastebin.com/api/api_login.php"
    private val LIST_USER_PASTES_URL  = "http://pastebin.com/api/api_post.php"
    private val LIST_TRENDING_URL     = "http://pastebin.com/api/api_post.php"
    private val DELETE_PASTE_URL      = "http://pastebin.com/api/api_post.php"
    private val GET_USER_INFO_URL     = "http://pastebin.com/api/api_post.php"
    private val RAW_OUTPUT_URL        = "http://pastebin.com/raw.php?i=%s"


    /**
      * Returns either a Success
      * or Failure.
      * @note Success can also be a failure to paste
      *       so all a Success means is that the POST
      *       request successfully went through and there
      *       were no network/HTTP related errors
      *
      * @param API_URL: String: URL of the specific POST request being made (ex: paste, delete, etc)
      * @param optionsMap: String: Map of POST request key/value parameters to be sent in the request
      * @param isRaw: Boolean (Default: false) telling if it's a raw output request or not
      * @param rawPasteKey: String: If it's a raw output request then a paste key is required (Paste Key is at end of paste URL)
      * @return [Success[String], Failure[Throwable]]
      *
      */
    protected def issueRequest(API_URL: S, optionsMap: => MS = Map(), isRaw: Boolean = false, rawPasteKey: S = ""): Try[S] = {

      // Form the request, issue the request, and return the response

      // If not raw then POST parameters need to be given
      if (!isRaw) {

        val request             = url(API_URL)
        val requestWithOptions  = (request << optionsMap).setHeader("charset", "UTF-8")
        val response            = Http(requestWithOptions OK as.String)
        Try(response())
      }

      // It's raw so no POST parameters need to be given
      else {

        val request             = url(API_URL.format(rawPasteKey))
        val response            = Http(request OK as.String)
        Try(response())
      }

    }

    /**
      * Returns either a Success
      * or Failure of newly created
      * user key.
      *
      * @note Success can also be a failure to paste
      *       so all a Success means is that the POST
      *       request successfully went through and there
      *       were no network/HTTP related errors
      *
      * @param userName: String: Username of user to generate key for
      * @param userPassword: String: Password of user to generate key for
      * @return [Success[String], Failure[Throwable]]
      *
      */
    def createAPIUserKey(userName: S = "", userPassword: S = ""): Try[S] = {

      // Create POST options for issuing the paste request
      lazy val userKeyOptions = Map("api_dev_key" -> devKey, "api_user_name" -> userName, "api_user_password" -> userPassword)

      issueRequest(USER_KEY_URL, userKeyOptions)
    }

    
    /**
      * Returns either a Success
      * or Failure of the newly
      * created pastebin URL.
      *
      * @note Success can also be a failure to paste
      *       so all a Success means is that the POST
      *       request successfully went through and there
      *       were no network/HTTP related errors
      *
      * @param codeToPaste: String: Code to submit for pasting
      * @param nameOfPaste: String: Name to serve as paste title
      * @param expireDate: String: How long the paste is accessible
      * @param syntaxHighlight: String: What programming language the code is in
      * @param userKey: String: User key to use if any
      * @param privateLevel: String: Listed, unlisted, or private
      * @return [Success[String], Failure[Throwable]]
      *
      */
    def makePaste(codeToPaste: S, nameOfPaste: S = "", expireDate: S = "",
                    syntaxHighlight: S = "", userKey: S = "", privateLevel: S = "0"): Try[S] = {

      // Create POST options for issuing the paste request
      lazy val pasteOptions = Map("api_option" -> "paste", "api_user_key" -> userKey, "api_paste_private" -> privateLevel,
                                "api_paste_name" -> nameOfPaste, "api_paste_expire_date" -> expireDate,
                                "api_paste_format" -> syntaxHighlight, "api_dev_key" -> devKey, "api_paste_code" -> codeToPaste)

      issueRequest(MAKE_PASTE_URL, pasteOptions)
    }

    /**
      * Returns either a Success
      * or Failure of a list of pastes
      * by logged in user.
      *
      * @note Success can also be a failure to paste
      *       so all a Success means is that the POST
      *       request successfully went through and there
      *       were no network/HTTP related errors
      *
      * @param userKey: String: User key to use (Required)
      * @param resultsLimit: String How many results to show
      * @return [Success[String], Failure[Throwable]]
      *
      */
    def listUserPastes(userKey: S, resultsLimit: S = "50"): Try[S] = {

      // Create POST options for issuing the list user pastes request
      lazy val listPasteOptions = Map("api_dev_key" -> devKey, "api_user_key" -> userKey,
                                    "api_results_limit" -> resultsLimit, "api_option" -> "list")

      issueRequest(LIST_USER_PASTES_URL, listPasteOptions)
    }

    /**
      * Returns either a Success
      * or Failure of a list of
      * trending pastes.
      *
      * @note Success can also be a failure to paste
      *       so all a Success means is that the POST
      *       request successfully went through and there
      *       were no network/HTTP related errors
      *
      * @return [Success[String], Failure[Throwable]]
      *
      */
    def listTrendingPastes: Try[S] = {

      // Create POST options for issuing the trending request
      lazy val trendingPasteOptions = Map("api_dev_key" -> devKey, "api_option" -> "trends")

      issueRequest(LIST_TRENDING_URL, trendingPasteOptions)
    }

    /**
      * Returns either a Success
      * or Failure of the result
      * of deleting a paste by the
      * logged in user.
      *
      * @note Success can also be a failure to paste
      *       so all a Success means is that the POST
      *       request successfully went through and there
      *       were no network/HTTP related errors
      *
      * @param userKey: String: User key of logged in user (Required)
      * @param pasteKey: String: Key of paste to be deleted (At the end of the URL)
      * @return [Success[String], Failure[Throwable]]
      *
      */
    def deletePaste(userKey: S, pasteKey: S): Try[S] = {

      // Create POST options for issuing the delete request
      lazy val deletePasteOptions = Map("api_dev_key" -> devKey, "api_user_key" -> userKey,
                                      "api_paste_key" -> pasteKey, "api_option" -> "delete")

      issueRequest(DELETE_PASTE_URL, deletePasteOptions)
    }

    /**
      * Returns either a Success
      * or Failure of info relating
      * to the logged in user.
      *
      * @note Success can also be a failure to paste
      *       so all a Success means is that the POST
      *       request successfully went through and there
      *       were no network/HTTP related errors
      *
      * @param userKey: String: User key of logged in user (Required)
      * @return [Success[String], Failure[Throwable]]
      *
      */
    def getUserInfo(userKey: S): Try[S] = {

      // Create POST options for issuing the get user info request
      lazy val userInfoOptions = Map("api_dev_key" -> devKey, "api_user_key" -> userKey, "api_option" -> "userdetails")

      issueRequest(GET_USER_INFO_URL, userInfoOptions)
    }

    /**
      * Returns either a Success
      * or Failure of raw paste output.
      *
      * @note Success can also be a failure to paste
      *       so all a Success means is that the POST
      *       request successfully went through and there
      *       were no network/HTTP related errors
      *
      * @param pasteKey: String: Paste key of paste to get raw output for (At the end of URL)
      * @return [Success[String], Failure[Throwable]]
      *
      */
    def getRawOutput(pasteKey: S): Try[S] = issueRequest(RAW_OUTPUT_URL, isRaw = true, rawPasteKey = pasteKey) // Issue raw output request

 }

 /**
   * Instantiates a PasteBinAPI class with a given
   * developer key.
   *
   * @param arg: String: Valid developer key
   *
   */
 def apply(arg: S) = new PasteBinAPI(arg)

}
