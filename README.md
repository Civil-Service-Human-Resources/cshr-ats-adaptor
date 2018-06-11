# cshr-ats-adaptor

## Docker

Build this image using

`docker build -t cshr-adaptor --build-arg arg_maven_repo="https://location.of.maven.repo/nexus/content/groups/public" .

## Running API

`make run` or `docker-compose up api`

## Stopping API

`make stop` or `docker-compose stop api`

## Running Tests
`make test` or `docker-compose run --rm test`

## Environment variables
The following variables need to be set when starting the container:

1. **ats.authentication.token** - A secret authentication token required by the ATS. See administrator for this value. 
1. **ats.client.id** - The ATS Client id that represents CSHR. See administrator for this value. 
1. **ats.jobrun.history.directory** - A default version exists in application.yml.  This variable indicates the directory of the process run history file used to determine when the process was last run. If this needs to be overriden supply the value when setting this environment variable. 
1. **ats.jobrun.history.file** - This variable indicates the directory of the process run history file used to determine when the process was last run. A default version exists in application.yml.  If this needs to be overriden supply the value when setting this environment variable. 
1. **ats.request.batch.size** - The size of the batch of records to process from ATS at one time. The maximum allowed is 100. A default version exists in application.yml.  If this needs to be overriden supply the value when setting this environment variable.
1. **ats.request.endpoint** - The endpoint for the ATS system. There are two endpoints available, one for testing and one for production. See administrator for this value. 
1. **cshr.api.service.vacancy.save.endpoint** - A default version exists in application.yml.  If this needs to be overriden supply the value when setting this environment variable. 
1. **cshr.api.service.department.findAll.endpoint** - A default version exists in application.yml.  If this needs to be overriden supply the value when setting this environment variable. 
1. **cshr.api.service.search.username** - A default version exists in application.yml.  If this needs to be overriden supply the value when setting this environment variable. 
1. **cshr.api.service.search.password** - A default version exists in application.yml.  If this needs to be overriden supply the value when setting this environment variable. 
1. **cshr.api.service.vacancy.save.username** - A default version exists in application.yml.  If this needs to be overriden supply the value when setting this environment variable. 
1. **cshr.api.service.vacancy.save.password** - A default version exists in application.yml.  If this needs to be overriden supply the value when setting this environment variable. 
1. **cshr.ats.vendor.id** - A default version exists in application.yml.  If this needs to be overriden supply the value when setting this environment variable.  
1. **cshr.jobrun.audit.directory** - The directory where process audit log files are written. A default version exists in application.yml.  If this needs to be overriden supply the value when setting this environment variable. 
1. **cshr.jobrun.audit.basefilename** - The basename of the process audit log filename. The system appends a datetimestamp to the base file name. A default version exists in application.yml.  If this needs to be overriden supply the value when setting this environment variable.
1. **cshr.jobrun.fixed.delay** - The fixed period of time in milliseconds between the end of one invocation of the ats vacancies load job and the start of the next one.
1. **slack.notification.channel** - The name of the channel on Slack to which the response for each instance of running the scheduled process is posted to.
1. **slack.notification.endpoint** - The endpoint for the Slack instance where the channel lives.
1. **spring.security.service.username** - The basic auth username required to access this service. The default developer value must be overridden for all non developer environments.
1. **spring.security.service.password** - The basic auth password required to access this service. The default developer value must be overridden for all non developer environments.