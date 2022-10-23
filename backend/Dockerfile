FROM maven:3.6-jdk-11
WORKDIR /backend
COPY generate_keys.sh deploy.sh pom.xml /backend/
RUN mvn -B dependency:resolve dependency:resolve-plugins -Pprod
COPY src /backend/src
RUN ./generate_keys.sh
RUN unset MAVEN_CONFIG && mvn package -Pprod -Dmaven.test.skip=true
ENTRYPOINT ./deploy.sh
