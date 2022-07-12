FROM mcr.microsoft.com/java/jdk:11-zulu-centos

WORKDIR /app
COPY build/libs/API_BE.jar .

ENTRYPOINT [ "sh", "-c", "java -Dspring.profiles.active=$SPRING_PROFILES_ACTIVE -jar API_BE.jar", \
"--SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE}", \
"--MYSQL_DATABASE=${MYSQL_DATABASE}", \
"--API_DB_USER=${API_DB_USER}", \
"--API_DB_PASS=${API_DB_PASS}", \
"--FIREBASE_SERVICE_ACCOUNT=${FIREBASE_SERVICE_ACCOUNT}" ]
