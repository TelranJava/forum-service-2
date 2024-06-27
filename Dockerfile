FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY .target/forum-service-0.0.1-SNAPSHOT.jar ./forum-service.jar

ENV MONGODB_URI=mongodb+srv://elena:12345.com@java52cluster.pupbm4c.mongodb.net/java52db?retryWrites=true&w=majority&appName=java52Cluster

CMD ["java","-jar", "/app/forum-service.jar"]
