# build environment
FROM adoptopenjdk:11-hotspot as build
ARG MAVEN_OPTS
WORKDIR /build
COPY . ./
RUN ./mvnw clean integration-test package $MAVEN_OPTS

# run environment
FROM adoptopenjdk:11-hotspot
ARG JAVA_OPTS
COPY --from=build /build/target/lib/* /app/lib/
COPY --from=build /build/target/*-runner.jar /app/
EXPOSE 8080
CMD ls /app/*-runner.jar | xargs java -jar $JAVA_OPTS
