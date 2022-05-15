# Images api
This is a project I do from time to time to experiment with newer versions with java, build tools, etc.

## Requirements
The latest version of `images-api` framework has the following minimal requirements:
- Java 8
- Gradle 6.8.3

### For installing the tools above
Use this tool to install different java versions or  gradle versions - https://www.baeldung.com/java-sdkman-intro

## Tests
This section will describe how to run tests and what technology used.
I currently have Junit5 unit and integration tests. Integration tests use h2 in memory database for storing or verifying data.
As it is a small project I have not split integration and unit tests into separate tasks. 

### Running tests from command line
To run tests locally run `gradle test`

# Running tests from IntelliJ
Go to File -> Settings -> Uuid, Execution, Deployment -> Build Tools -> Gradle
Set setting `Run tests using` to IntelliJ IDEA instead of gradle

## How to build it
On source directory run command `./gradlew bootRun` - you will need mysql server running on `localhost` on port `3306`. 
The schema will be created automatically.

## Things to do to: 
There are a few things that I still need to do:
- Add GitHub actions to run application tests and build docker image - I am more familiar with CircleCi or Bitbucket pipelines
- Add docker-compose file to build application and plugin mysql database - this is just a small project so going for simple and small configuration
- Add new controller to get notification messages - move into it's own package
- Build small consumer of the api
- Configure images storage - best would be s3 bucket where person using the api could register themselves by supplying s3 bucket url or any other storage link
