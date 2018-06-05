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

FROM 	cshrrpg.azurecr.io/java-8-base-filebeat

COPY 	--from=build /usr/src/myapp/target/ats-adaptor.jar /app/ats-adaptor.jar

COPY  --chown=appuser:appuser entrypoint.sh /usr/local/bin/entrypoint.sh

RUN   chmod 755 /usr/local/bin/entrypoint.sh

USER 	appuser

ENTRYPOINT [  "/usr/local/bin/entrypoint.sh" ]
