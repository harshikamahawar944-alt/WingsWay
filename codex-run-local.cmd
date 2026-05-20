@echo off
setlocal
set "JAVA_HOME=C:\Users\Dell\.vscode\extensions\redhat.java-1.54.0-win32-x64\jre\21.0.10-win32-x86_64"
set "PATH=%JAVA_HOME%\bin;%PATH%"
call mvnw.cmd spring-boot:run
