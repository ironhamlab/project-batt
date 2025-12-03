# Jenkins가 빌드한 JAR 파일 사용하는 방식
FROM eclipse-temurin:21-jre

# 작업 디렉토리 설정
WORKDIR /app

# JAR 파일 복사 (Jenkins에서 빌드된 것 사용 예정)
COPY build/libs/batt-0.0.1-SNAPSHOT.jar app.jar

# XLARGE 환경에 최적화된 JVM 옵션
EXPOSE 8080
ENTRYPOINT ["java", \
    "-Xmx2g", \
    "-Xms512m", \
    "-XX:+UseG1GC", \
    "-XX:G1HeapRegionSize=16m", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=80.0", \
    "-XX:+OptimizeStringConcat", \
    "-XX:+UseStringDeduplication", \
    "-Dspring.profiles.active=docker", \
    "-jar", "app.jar"]