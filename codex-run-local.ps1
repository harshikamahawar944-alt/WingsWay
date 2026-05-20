$env:JAVA_HOME = 'C:\Users\Dell\.vscode\extensions\redhat.java-1.54.0-win32-x64\jre\21.0.10-win32-x86_64'
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
& .\mvnw.cmd spring-boot:run
