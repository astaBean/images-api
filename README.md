# Images api
This is a project I do from time to time to experiment with newer versions with java, build tools, etc.

## Requirements
The latest version of `images-api` framework has the following minimal requirements:
- Java 11
- Gradle 6.8.3

### For installing the tools above
Use this tool to install different java versions or  gradle versions - https://www.baeldung.com/java-sdkman-intro

## Tests
This section will describe how to run tests and what technology used.
I currently have Junit5 unit and integration tests. Integration tests use h2 in memory database for storing or verifying data.
As it is a small project I have not split integration and unit tests into separate tasks. 

### Running tests from command line
To run tests locally run `gradle test`
**Note:** If you do not have gradle run `./gradelw test` from root directory

# Running tests from IntelliJ
Go to File -> Settings -> Uuid, Execution, Deployment -> Build Tools -> Gradle
Set setting `Run tests using` to IntelliJ IDEA instead of gradle

## How to run the application locally
Before running the application you will need you will need mysql 8 server running locally, and it has to be accessible  on `localhost` port `3306`. 
I included mysql 8 server part of docker-compose.yml file.

### How to run mysql server locally
To start mysql service run in project directory `docker-compose up -d` command which runs docker services described in docker-compose.yml file in detached mode.
To see logging of mysql server you can run `docker logs -f images-api_database_1` command. Helpful if you need to diagnose why your application doesn't connect to the service.

Now you are set and ready to run application. To do that run command `./gradlew run` or use `gradle run` depending if you have locally gradle installed. If not use `gradlew` example.
You can access your application on `http://localhost:8080`

## How do you access apis in this project
When the application is running to access application api endpoints you need to use api tool like postman or curl that gives an ability to submit api requests. 

### Supported apis - this would be much better in openApi documentation
- GET `http://localhost:8080/image/all` - gets all images
- POST 'http://localhost:8080/image with form parameters - creates an image
  - description=%%ANY_STRING%%
  - title=%%ANY_STRING%%
  - file=%%FILE_LOCATION%% - example `@"/home/%%USER%%/Pictures/randomPic.png`

- PUT `http://localhost:8080/image` with form parameters updates an image
  - description=%%ANY_STRING%%
  - title=%%ANY_STRING%%
  - file=%%FILE_LOCATION%% - example `@"/home/%%USER%%/Pictures/randomPic.png`
  - uuid=%%UNIQUE_IDENTIFIER%% - you can get it by doing a request to get all images; Also ths value is returned from newly create image

- GET `http://localhost:8080/image/${uuid}` - get image by image identifier
- DELETE `http://localhost:8080/image/${uuid}` - delete an image


## Things to do to:
Apis currently work in tests but do now work properly when application is running. Find out why!! - Probably because uuid is saved as blob in db

There are a few other things that I still need to do:
- Return a message when image has been deleted successfully
- Add more validation on endpoints (add required and non required fields)
- Allow submission of partial data
- Handle errors when file is missing
- Add GitHub actions to run application tests and build docker image - I am more familiar with CircleCi or Bitbucket pipelines
- Add docker-compose file to build application and plugin mysql database - this is just a small project so going for simple and small configuration
- Add new controller to get notification messages - move into it's own package
- Build small consumer of the api
- Configure images storage - best would be s3 bucket where person using the api could register themselves by supplying s3 bucket url or any other storage link
- Add api documentation OpenApi
