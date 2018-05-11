FROM 	cshrrpg.azurecr.io/maven-3-base as dependencies

ARG	    arg_maven_repo

ENV 	MAVEN_REPO=$arg_maven_repo

COPY 	pom.xml /usr/src/myapp/pom.xml
COPY 	.settings.xml /usr/share/maven/ref/settings.xml

RUN 	mvn -f /usr/src/myapp/pom.xml -s /usr/share/maven/ref/settings.xml dependency:resolve


FROM 	cshrrpg.azurecr.io/maven-3-base as build

ARG	    arg_maven_repo
ENV	    MAVEN_REPO=$arg_maven_repo

COPY 	--from=dependencies /usr/share/maven/ref/ /usr/share/maven/ref/

COPY 	pom.xml /usr/src/myapp/pom.xml
COPY 	. /usr/src/myapp/

RUN 	mvn -f /usr/src/myapp/pom.xml -s /usr/share/maven/ref/settings.xml clean package

FROM 	cshrrpg.azurecr.io/java-8-base

COPY 	--from=build /usr/src/myapp/target/ats-adaptor-0.0.1.jar /app/ats-adaptor-0.0.1.jar

USER 	appuser

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom","-jar","/app/ats-adaptor-0.0.1.jar", \
            "--ats.authentication.token=${ATS_AUTHENTICATION_TOKEN}", \
            "--ats.client.id=${ATS_CLIENT_ID}", \
            "--ats.jobrun.history.directory=${ATS_JOBRUN_HISTORY_DIRECTORY}", \
            "--ats.jobrun.history.file=${ATS_JOBRUN_HISTORY_FILE}", \
            "--ats.request.batch.size=${ATS_REQUEST_BATCH_SIZE}", \
            "--ats.request.endpoint=${ATS_REQUEST_ENDPOINT}", \
            "--cshr.api.service.department.findall.endpoint=${CSHR_API_SERVICE_DEPARTMENT_FINDALL_ENDPOINT}", \
            "--cshr.api.service.vacancy.save.endpoint=${CSHR_API_SERVICE_VACANCY_SAVE_ENDPOINT}", \
            "--cshr.api.service.vacancy.save.username=${CSHR_API_SERVICE_VACANCY_SAVE_USERNAME}", \
            "--cshr.api.service.vacancy.save.password=${CSHR_API_SERVICE_VACANCY_SAVE_PASSWORD}", \
            "--cshr.api.service.search.username=${CSHR_API_SERVICE_SEARCH_USERNAME}", \
            "--cshr.api.service.search.password=${CSHR_API_SERVICE_SEARCH_PASSWORD}", \
            "--cshr.ats.vendor.id=${CSHR_ATS_VENDOR_ID}", \
            "--cshr.jobrun.audit.directory=${CSHR_JOBRUN_AUDIT_DIRECTORY}", \
            "--cshr.jobrun.audit.basefilename=${CSHR_JOBRUN_AUDIT_BASEFILENAME}", \
            "--server.port=${SERVER_PORT}", \
            "--spring.security.service.username=${SPRING_SECURITY_SERVICE_USERNAME}", \
            "--cshr.api.service.vacancy.load.endpoint=${CSHR_API_SERVICE_VACANCY_LOAD_ENDPOINT}", \
            "--cshr.api.service.vacancy.findAll.endpoint=${CSHR_API_SERVICE_VACANCY_FINDALL_ENDPOINT}", \
            "--spring.security.service.password=${SPRING_SECURITY_SERVICE_PASSWORD}"]
