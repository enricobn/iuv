### Install iuv-core
Since it's not in a remote repository, for now, you have to install locally:  
`./gradlew iuv:iuv-core install`

### Compile and run the test page
When you change (and install) a new version on the iuv-core library you have to delete the examples/buttons/out folder.
From intellij you have to build the project (Ctrl-F9).
Then right click on examples/buttons/web/test.html -> open in browser.