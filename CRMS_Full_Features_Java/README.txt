CRIME RECORD MANAGEMENT SYSTEM - FULL JAVA BACKEND

REQUIREMENTS
- JDK 17 or later (your installed JDK 21 is suitable)
- Windows PowerShell

RUN ON WINDOWS
1. Extract the ZIP.
2. Open PowerShell in the extracted CRMS_Full_Features_Java folder (the folder containing src and web).
3. Compile:
   javac -d out src\*.java
   If PowerShell/Javac reports the wildcard literally, use:
   javac -d out src\Main.java src\Json.java src\Person.java src\Criminal.java src\Officer.java src\CrimeRecord.java src\RecordManager.java src\InvalidRecordException.java src\CrimeRecordManager.java
4. Run:
   java -cp out Main
5. Open http://localhost:8765

LOGIN
Username: admin
Password: admin123

FEATURES
- Dashboard: totals, case status counts, wanted criminals, crime-type chart, latest records
- Crime records: create, view, edit, delete, keyword search, status/type filters
- Assign an investigating officer and suspect to a crime record
- Criminals: create, view, edit, delete; age/status validation and linked-case counts
- Officers: create, view, edit, delete; rank/badge validation and duplicate badge prevention
- CSV export of crime records
- Automatic demo data: 4 crimes, 3 criminals, 3 officers on first run
- Java object serialization to data/crms-data.ser; data survives restarts
- Deleting a criminal/officer clears references from associated crime records

PROJECT STRUCTURE
src\Person.java, Criminal.java, Officer.java, CrimeRecord.java: model classes
src\RecordManager.java: interface
src\CrimeRecordManager.java: collections, CRUD, demo seeding and persistence
src\InvalidRecordException.java: custom exception class
src\Json.java: small JSON serializer for HTTP responses
src\Main.java: Java HTTP server and API
web\index.html, style.css, app.js: browser presentation layer

ARCHITECTURE NOTE
The application logic, validation, data management and persistent storage are implemented in Java.
The browser interface uses HTML and CSS, with JavaScript only to submit HTTP requests and render
the server responses. This is not a production police information system. The login is a fixed
academic demo account and must not be used for real sensitive data.
