@echo off
echo Building JAR file...
call mvn clean package
echo JAR file created successfully at target/sprint-1.0-SNAPSHOT.jar
echo Dependencies copied to target/lib/
pause
