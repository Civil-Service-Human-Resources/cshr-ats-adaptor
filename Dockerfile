FROM 	maven:3.5-jdk-8 as dependencies

ARG	    arg_maven_repo

ENV 	MAVEN_REPO=$arg_maven_repo

COPY 	pom.xml /usr/src/myapp/pom.xml
COPY 	.settings.xml /usr/share/maven/ref/settings.xml

RUN 	mvn -f /usr/src/myapp/pom.xml -s /usr/share/maven/ref/settings.xml dependency:resolve


FROM 	maven:3.5-jdk-8 as build

ARG	    arg_maven_repo
ENV	    MAVEN_REPO=$arg_maven_repo

COPY 	--from=dependencies /usr/share/maven/ref/ /usr/share/maven/ref/

COPY 	pom.xml /usr/src/myapp/pom.xml
COPY 	. /usr/src/myapp/

RUN 	mvn -f /usr/src/myapp/pom.xml -s /usr/share/maven/ref/settings.xml clean package

FROM 	openjdk:8u151-jre-alpine3.7

COPY 	--from=build /usr/src/myapp/target/ats-adaptor-0.0.1.jar /ats-adaptor-0.0.1.jar

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom","-jar","/ats-adaptor-0.0.1.jar" ]
