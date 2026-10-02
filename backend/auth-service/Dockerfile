# Dockerfile del servicio para Render. El contexto de construcción es la
# carpeta de este servicio (ver render.yaml en la raíz del repo).

FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /src
COPY . .
RUN mvn -q -B -DskipTests package && cp target/*.jar /app.jar

FROM eclipse-temurin:17-jre
COPY --from=build /app.jar /app.jar
# Render indica el puerto en $PORT. Memoria ajustada a los 512 MB del plan gratis.
CMD ["sh", "-c", "java -Xss256k -XX:+UseSerialGC -XX:TieredStopAtLevel=1 -XX:MaxRAMPercentage=70 -Dspring.jpa.show-sql=false -jar /app.jar --server.port=${PORT:-8080}"]
