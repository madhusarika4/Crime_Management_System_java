@echo off
title Crime Record Management System
cd /d "%~dp0"
if not exist out mkdir out
echo Compiling Crime Record Management System...
javac -d out src\Main.java src\Json.java src\Person.java src\Criminal.java src\Officer.java src\CrimeRecord.java src\RecordManager.java src\InvalidRecordException.java src\CrimeRecordManager.java
if errorlevel 1 (
  echo.
  echo Compilation failed. Check the error messages above.
  pause
  exit /b 1
)
echo.
echo Starting the Java server...
echo Open http://localhost:8765 in your browser.
echo Demo login: admin / admin123
echo Keep this window open. Press Ctrl+C to stop the server.
java -cp out Main
pause
