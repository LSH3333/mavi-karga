FROM openjdk:17-jdk-slim
# /build/libs/ 디렉토리의 jar 파일을 도커 이미지에 복사하고 app.jar 로 이름 변경
ADD /build/libs/*.jar app.jar
# 도커 컨테이너 실행 시 실행 될 명령어
# java -jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]