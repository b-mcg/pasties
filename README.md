pasties
--------
--------

pasties is a simple interface to the pastebin API.
It requires that you have a valid developer key which
you can obtain just by creating a free account with them.


Example Usage
--------------
--------------
    Create a new paste:

        ```scala
        import pasties.PasteBinAPI
        import scala.util.{Try, Success, Failure}

        // Instantiate the interface
        val APIInterface = PasteBinAPI("Whatever your developer key is")

        // Create some code to be pasted
        val codeToPaste = """
                            /**
                              * This is a new paste!
                              *
                              */

                            // Simple factorial function using the foldLeft method
                            val fact = (n: BigInt) => (BigInt(1) to n).foldLeft (BigInt(1)) (_ * _)
                          """.stripMargin

       // Call the method responsible for sending the POST request to pastebin's API
       // See the source file: PasteBinAPI.scala for a full listing of parameters and their significance or see the scaladoc file
       // All parameters default to an empty string except for the one accepting the code to paste
       val pasteResult = APIInterface.makePaste(codeToPaste, "A test paste" //This is the name of the paste file, "10M" // This sets the expire time,
                                "scala" // This sets the syntax highlighting, privateLevel = "1" // This sets the privacy level)

       // Consult the pastebin API documentation for a full listing of supported syntax highlights
       
       // Note: The return type of every method is either a Success[String] or a Failure[Throwable]
       ```

    Delete A Paste:
        
        ```
        // Let's create a function real quick in order to extract the result from our responses
        def extract(result: Try[String]): String = result match {

            case Success(value) => value

            case Failure(err)   => "Error: Something went wrong"
        }

        // Note: This method requires a user key which can be easily created using the createAPIUserKey method
        //       which will be shown next.  Also, in order to use this method you must have created a paste logged
        //       in with your user key.  Example:
        val myUserPaste = APIInterface.makePaste(codeToPaste, "A test paste" //This is the name of the paste file,
                                                    "10M" // This sets the expire time, "scala" // This sets the syntax highlighting,
                                                    "my user key", "1" // This sets the privacy level)


        val deleteResponse = APIInterface.deletePaste("my user key",
                                                        extract(myUserPaste).split('/').last // This grabs the paste key from the paste URL)
        ```

    Create User Key:
        
        ```
        val userKey = APIInterface.createAPIUserKey("your username", "your password")
        ```

    List Pastes by You:

        ``` 
        // This method simply returns pastes you've made
        // If a good response is returned it will be a string of xml, but as mentioned earlier it is either
        // a Success[String] or a Failure[Throwable]
        // Note: this method also requires a valid user key
        val pastesByMe = APIInterface.listUserPastes(extract(userKey))

        // The default limit is set to 50.  Minimum is 1 and maximum is 1000.
        // Example for getting back 30 results:
        val pastesByMe = APIInterface.listUserPastes(extract(userKey), "30")
        ```

    List Trending Pastes:

        ```
        // This method lists the top 18 trending pastes on pastebin
        val top18 = APIInterface.listTrendingPastes
        ```

    Get User Info:

        ```
        // This method returns information relating to the currently logged in user
        val myInfo = APIInterface.getUserInfo(extract(userKey)) // The only argument for this method is a valid user key
        ```

    Get Raw Output of a paste:

        ```
        // This method returns the raw output of a given paste
        // Let's get the raw output of the initial paste
        val rawResult = APIInterface.getRawOutput(extract(pasteResult).split('/').last // Grab the paste key at the end of the URL)
        ```


That's it, those are all the actions that the pastebin API supports.
