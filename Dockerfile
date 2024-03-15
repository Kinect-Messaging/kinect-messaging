FROM eclipse-temurin:17-jdk-alpine as build
WORKDIR /workspace/app

RUN addgroup -S dgroup && adduser -S duser -G dgroup
USER duser

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY libs libs
COPY apps apps


RUN ./mvnw install -DskipTests

# Build apps/config
RUN mkdir -p apps/config/target/dependency && (cd apps/config/target/dependency; jar -xf ../*.jar)
RUN mkdir -p libs/common-utils/target/dependency && (cd libs/common-utils/target/dependency; jar -xf ../*.jar)


FROM eclipse-temurin:17-jre-alpine
VOLUME /tmp

RUN addgroup -S dgroup && adduser -S duser -G dgroup
USER duser

ARG DEPENDENCY=/workspace/app/apps/config/target/dependency
ARG LIB_DEPENDENCY=/workspace/app/libs/common-utils/target/dependency
ARG DESCRIPTION="Config Container for Kinect Messaging"
LABEL org.opencontainers.image.description = ${DESCRIPTION}
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app
COPY --from=build ${LIB_DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${LIB_DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${LIB_DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-cp","app:app/lib/*","com.kinect.messaging.config.KinectConfigApplication"]