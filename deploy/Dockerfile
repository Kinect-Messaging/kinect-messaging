FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /workspace/app

ARG APP_NAME=$APP_NAME

COPY ../mvnw .
COPY ../.mvn .mvn
COPY ../pom.xml .
COPY ../apps apps/

RUN ./mvnw install -DskipTests

# Build app
RUN mkdir -p apps/$APP_NAME/target/extracted
RUN java -Djarmode=layertools -jar apps/$APP_NAME/target/*.jar extract --destination apps/$APP_NAME/target/extracted


FROM eclipse-temurin:17-jre-alpine
VOLUME /tmp

RUN addgroup -S dgroup && adduser -S duser -G dgroup
USER duser

ARG APP_NAME=$APP_NAME
ARG EXTRACTED=/workspace/app/apps/$APP_NAME/target/extracted
ARG DESCRIPTION="$APP_NAME Container for Kinect Messaging"
LABEL org.opencontainers.image.description = ${DESCRIPTION}
COPY --from=build ${EXTRACTED}/dependencies/ ./
COPY --from=build ${EXTRACTED}/spring-boot-loader/ ./
COPY --from=build ${EXTRACTED}/snapshot-dependencies/ ./
COPY --from=build ${EXTRACTED}/application/ ./

ENTRYPOINT ["java","org.springframework.boot.loader.launch.JarLauncher"]