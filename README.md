# cshr-ats-adaptor
## Overview
The purpose of this microservice is to allow a request to be made to an external applicant tracking system (ATS) in order to receive information about live vacancies held in the ATS.

The microservice will validate and transform the incoming collection of vacancies into a format that can be persisted into CSHR's Vacancy data store that backs the CSHR Candidate Interface.

## To build
run 
'mvn clean package' on your local environment.

## To run
Either use your ide or the command line to launch the microservice built.

OAuth2 is turned on with an in memory embedded capability.  By default a single set of user and client credentials will be used to spin up the OAuth2 configuraiton and server.

You may override these default values and supply your own with the following environment variables:

'security.oauth2.client.clientId'
'security.oauth2.client.clientSecret'
'spring.user.name'
'spring.user.password'

Either set these as environment variables in your IDE's run time configuration for the application or via the command line as follows:

'java -jar ats-adaptor-0.0.1.jar --security.oauth2.client.clientId=boo --security.oauth2.client.clientSecret=hoo --spring.user.name=foo --spring.user.password=bar'

The duration of the token and the refresh token can also be overridden when starting the microservice.  The default values are:

security.oauth2.refresh.token.duration = 240 seconds

security.oauth2.token.duration = 120 seconds