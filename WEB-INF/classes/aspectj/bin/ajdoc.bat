@echo off
REM This file generated by AspectJ installer
REM Created on Sun Oct 10 14:00:28 PDT 2021 by kevyu

if "%JAVA_HOME%" == "" set JAVA_HOME=C:\Program Files\Java\jdk-11.0.12
if "%ASPECTJ_HOME%" == "" set ASPECTJ_HOME=C:\BCIT\COMP3940\Assignment-2\WEB-INF\classes\aspectj

if exist "%JAVA_HOME%\bin\java.exe" goto haveJava
if exist "%JAVA_HOME%\bin\java.bat" goto haveJava
if exist "%JAVA_HOME%\bin\java" goto haveJava
echo java does not exist as %JAVA_HOME%\bin\java
echo please fix the JAVA_HOME environment variable
:haveJava
"%JAVA_HOME%\bin\java" -classpath "%ASPECTJ_HOME%\lib\aspectjtools.jar;%JAVA_HOME%\lib\tools.jar;%CLASSPATH%" -Xmx64M org.aspectj.tools.ajdoc.Main %*
