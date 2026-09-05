FROM eclipse-temurin:17-jdk-jammy AS build

WORKDIR /workspace

COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw --batch-mode --no-transfer-progress dependency:go-offline

COPY src src
RUN ./mvnw --batch-mode --no-transfer-progress package -DskipTests

FROM eclipse-temurin:17-jre-jammy

RUN groupadd --system dispatch \
    && useradd --system --gid dispatch --home-dir /app dispatch

WORKDIR /app
COPY --from=build /workspace/target/*.jar app.jar

USER dispatch
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]

