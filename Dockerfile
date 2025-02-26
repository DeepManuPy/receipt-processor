FROM eclipse-temurin:17-jdk AS build
WORKDIR /workspace/app

# Install Maven
RUN apt-get update && apt-get install -y maven

# Copy only necessary files for build
COPY pom.xml .
COPY src src

# Build the application
RUN mvn install -DskipTests
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

FROM eclipse-temurin:17-jre
VOLUME /tmp
ARG DEPENDENCY=/workspace/app/target/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-cp","app:app/lib/*","com.deepmanupy.receipt_processor.ReceiptProcessorApplication"]