# ---------- Etapa 1: build ----------
# Imagen con JDK + Maven para compilar. No llega a la imagen final.
FROM docker.io/library/maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Primero solo el pom: si no cambian las dependencias, Docker reutiliza esta capa en cache.
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

# Después el código fuente y empaquetado (los tests se corren en CI/local, no en el build de la imagen).
COPY src ./src
RUN mvn -B -q package -DskipTests \
 && cp target/*.jar app.jar

# ---------- Etapa 2: runtime ----------
# Solo JRE: imagen más pequeña y con menos superficie de ataque.
FROM docker.io/library/eclipse-temurin:21-jre
WORKDIR /app

# Usuario sin privilegios: si alguien compromete la app, no es root dentro del contenedor.
RUN groupadd --system spring && useradd --system --gid spring spring
USER spring

COPY --from=build /app/app.jar app.jar

EXPOSE 8080

# Variables requeridas en tiempo de ejecución (NO se hornean en la imagen):
#   DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD, API_KEY
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0"
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
