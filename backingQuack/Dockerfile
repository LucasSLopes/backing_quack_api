# Use a imagem base do Maven para compilar o projeto
FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /app

# Copie o arquivo pom.xml e a pasta src para o contêiner
COPY pom.xml .
COPY src ./src

# Compile o projeto e crie o pacote
RUN mvn clean package -DskipTests

# Use uma imagem base do OpenJDK para rodar a aplicação
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copie o arquivo JAR do estágio de build para o estágio final
COPY --from=build /app/target/*.jar app.jar

# Exponha a porta em que a aplicação irá rodar
EXPOSE 8080

# Comando para rodar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]

