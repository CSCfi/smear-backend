# build environment
FROM adoptopenjdk:11-hotspot as build
ARG MAVEN_OPTS
WORKDIR /build
COPY . ./
RUN ./mvnw clean integration-test package $MAVEN_OPTS

# run environment
FROM adoptopenjdk:11-hotspot
ARG JAVA_OPTS
COPY --from=build /build/target/quarkus-app /app/quarkus-app
EXPOSE 8080
CMD java -jar $JAVA_OPTS /app/quarkus-app/quarkus-run.jar
