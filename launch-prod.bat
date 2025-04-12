@echo off
setlocal

REM Load environment variables from .env file
for /F "tokens=*" %%A in (.env) do set %%A

echo Starting application build...
call ./mvnw clean package -DskipTests

echo Setting up environment variables...
set SPRING_PROFILES_ACTIVE=prod
set PORT=8080

echo Starting the application...
java -jar ^
-Dspring.profiles.active=prod ^
-Dserver.port=%PORT% ^
-DAIVEN_DATABASE_URL=%AIVEN_DATABASE_URL% ^
-DAIVEN_DATABASE_USERNAME=%AIVEN_DATABASE_USERNAME% ^
-DAIVEN_DATABASE_PASSWORD=%AIVEN_DATABASE_PASSWORD% ^
-DJWT_SECRET=%JWT_SECRET% ^
target/secure-api.jar

pause 