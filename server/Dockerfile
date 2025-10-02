
FROM eclipse-temurin:17-jdk-jammy AS build

RUN apt update
RUN apt install dos2unix

#Copiar el código fuente y archivos de compilación a la imagen

#Compilar el código
COPY src /build/src
COPY pom.xml /build
COPY mvnw /build
COPY .mvn /build/.mvn

RUN dos2unix /build/mvnw
WORKDIR /build
RUN ./mvnw package

#Segunda etapa: no necesita dependencias, solo Java y el .jar
FROM eclipse-temurin:17-jre-jammy AS final
COPY --from=build /build/target/*.jar /app/
WORKDIR /app
EXPOSE 8080

ENTRYPOINT [ "sh", "-c" ]
CMD ["exec java -jar -Dspring.profiles.active=docker /app/*.jar"]