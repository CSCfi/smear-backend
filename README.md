# smear-backend project

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```
./mvnw quarkus:dev
```

## Packaging and running the application

The application can be packaged using `./mvnw package`.
It produces the `smear-backend-1.0-SNAPSHOT-runner.jar` file in the `/target` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/lib` directory.

The application is now runnable using `java -jar target/smear-backend-1.0-SNAPSHOT-runner.jar`.

## Deploying to OpenShift

Create a secret named `smear-backend-db-secrets` containing the database settings 
(see `src/main/resources/application.properties`).

The application can be deployed to OpenShift with `./mvnw clean package -Dquarkus.kubernetes.deploy=true`.
This will build and push the image to the registry, then deploy the app.

To only build and push the image run `./mvnw clean package -Dquarkus.container-image.build=true`.
