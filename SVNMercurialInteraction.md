# Summary #

This wiki page contains information about how the dual repository approach works. It may be of great help if you never worked on a svn branch in a remote repository before.

## Clone ##

Do a simple `hg clone https://jdownloader-service.googlecode.com/hg/ JDownloader` to get the latest source from the branch. Remember that this won't enable you to synchronize your source with the main projects SVN repository. The project is form time to time synchronized with the SVN repository and you receive those changes when you pull them from the hg repository. But if you plan to do SVN imports you have to do a little bit more than just cloning the hg repository. It is slightly more complicated and below is described how that works.

## SVN and Clone ##

This small howto describes how to synchronize the branch hg repository with the trunk SVN repository. Practically it is a dualistic project checkout where both repositories work on the same source files but don't know of each other.

  1. `cd ?EclipseWorkspace`
> > change to the eclipse workspace directory
  1. `svn checkout svn://svn.jdownloader.org/jdownloader/ JDownloader`
> > checkout the svn trunk
  1. `hg clone https://jdownloader-service.googlecode.com/hg/ JDownloader-hg`
> > clone the hg repository
  1. `mv JDownloader-hg\.hg JDownloader`
> > move hg meta-data to the svn trunk
  1. `rm -rf JDownloader-hg`
> > remove the no longer required directory
  1. `cd JDownloader`
> > change to the project working directory
  1. `hg revert`
> > revert all changes to the hg tip
  1. now you have the project in synch with hg and the SVN repository
  1. please follow the steps above to import SVN changes into the mercurial repository

## How to import SVN changes ##

Once done above checkout/clone, follow these steps:

  1. `hg up ?rev`
> > where ?rev is the last hg revision where this operation was done
  1. `svn up`
> > then we have the latest source from SVN
  1. `hg add`
> > add all new files; please don't add .svn folders that are now ignored
  1. `hg remove --after`
> > remove all files that are now missing
  1. `hg commit`
> > commit the svn changes; please provide a nice log message like "hg synch with SVN rev X"
  1. `hg merge tip`
> > which should merge the svn import into the tip
  1. `hg up`
> > update to the latest tip and start coding again with a up-to-date trunk