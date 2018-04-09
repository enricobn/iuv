### Install iuv-core
Since it's not in a remote repository, for now, you have to install locally:  
`./gradlew iuv:iuv-core install`

### Compile, test and run the examples pages
Run the build task for project examples/buttons. From the command line you can run `./gradlew examples/buttons:build`.
That task creates a web folder under build, then you have to run a web server to serve that folder.  
A simple solution in IntelliJ is to right click on index.html.html -> open in browser, but I have experienced some problems with source maps.  
Another simple way that solves those problems, if you have python installed, is:  
`cd examples/buttons/build/web`  
`python -m SimpleHTTPServer [port]`  
It will run a server listening on the specified port, or 8000 if not specified.

### Compile and run the unit tests
TODO 
