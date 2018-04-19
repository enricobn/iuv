### Install iuv-core
Since it's not in a remote repository, for now, you have to install locally:  
`./gradlew iuv:iuv-core install`

### Compile, test and run the examples pages
Run the build task for project examples/buttons. From the command line you can run `./gradlew examples/buttons:build`.
The first time it will be slow since it will download nodejs, yarn and all javascript libraries.
However nodejs is cached in the .gradle home folder, so it will be downloaded only one time per user.  

That task creates a web folder under build, then you have to run a web server to serve that folder.  

A simple solution in IntelliJ is to right click on index.html -> open in browser, but I have experienced problems 
with source maps.  

Another simple way that solves those problems, if you have python installed, is:  
`cd examples/buttons/build/web`  
`python -m SimpleHTTPServer [port]`  
It will run a server listening on the specified port, or 8000 if not specified.

### Compile and run the unit tests
TODO 

### Patterns ###
UV and IUV:
- since the model must be public, if you want to protect its construction from outside (almost, since it's frequently 
a data class, and the copy method is still accessible), make the model's constructor private and create a companion 
object with a factory method.  
If the UV is not an object and you need some instance val to create the initial model (or it's a IUV which must have 
an init member function), create an init member function of UV (or the needed one in IUV) and call the factory with the 
needed vals
- if not in the above example, for a UV there's no need for an init member function to initialize the model, use
the model's constructor, so you don't need an instance of UV to create it, or a factory if there's some logic
or default values.

