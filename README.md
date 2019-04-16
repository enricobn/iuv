# IUV #
Kotlin js UI framework inspired to Elm 
(http://elm-lang.org)

### Import in Intellij IDEA ###
Import as a gradle project check: 
* create separate module for source set
* store generated project files externally
and use Default gradle wrapper 

### Compile, test and run the examples
From the command line : `./gradlew build` 

The first time it will be slow since it will download node js, yarn and all javascript libraries.

That task creates a "web" folder under iuv-examples/build, then you have to open index.html with a browser.  

### Build command line tool
From the command line : `./gradlew iuv-openapi-codegen:shadowJar`  

An executable jar file in iuv-openapi-codegen/build/libs named *-all.jar should 
be created.

### Install
Since it's not in a remote repository, for now, you have to install locally. From the command line :  
`./gradlew publishToMavenLocal`

This task will install the libraries in the local maven repository.
