FROM maven:3-eclipse-temurin-17-alpine
WORKDIR /backend
COPY generate_keys.sh deploy.sh pom.xml /backend/
RUN mvn -B dependency:resolve dependency:resolve-plugins -Pprod
COPY src /backend/src
RUN ./generate_keys.sh
RUN mvn package -Pprod -Dmaven.test.skip=true
EXPOSE 8080
CMD ["./deploy.sh"]
