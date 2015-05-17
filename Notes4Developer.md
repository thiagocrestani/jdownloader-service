# Summary #

This wiki page contains notes on this project that developers could interest. It explains how the code structure is, how one can get involved and also important things about classpath dependencies and other things that can become frustrating problems if one doesn't know about them. Please also note that this wiki page could potentially be out of date. So just regard this to be only a good hint, but nothing more.

## Code Structure ##

All code is contained within the package `jd.service` that can be found in the main project source folder ([Browse Source Online](http://code.google.com/p/jdownloader-service/source/browse/#hg/src/jd/service)). This should greatly improve and ease merge processes in the future.

## Classpath Dependencies ##

Following classpath dependencies are additionally needed in order to run the JDownloader service:

  * commons-logging-1.1.jar
  * ws-commons-util-1.0.2.jar
  * xmlrpc-client-3.1.3.jar
  * xmlrpc-common-3.1.3.jar
  * xmlrpc-server-3.1.3.jar

## Get Involved ##

If you want to help feel free to clone the repository and patchbomb the project mailing list. Once the project team leaders gain some trust, they will grant you push permissions on the repository.