# build environment
FROM adoptopenjdk:11-hotspot as build
WORKDIR /build
COPY . ./
RUN ./mvnw clean integration-test package

# run environment
FROM adoptopenjdk:11-jre-hotspot
COPY --from=build /build/target/lib/* /app/lib/
COPY --from=build /build/target/*-runner.jar /app/app.jar
EXPOSE 8080
CMD ["java", "-jar", "/app/app.jar"]
