@echo off
title CourseToICS - Local Server
cd /d "%~dp0"

echo.
echo   ==========================================
echo     Course to ICS  -  Timetable to Calendar
echo   ==========================================
echo.

rem If server is already running, just open the browser
curl -s -o nul http://localhost:8080/api/v1/health
if not errorlevel 1 (
  echo Server already running. Opening browser...
  start "" http://localhost:8080/
  timeout /t 3 >nul
  exit /b 0
)

rem First run or target was cleaned: build the jar automatically
if not exist "target\course-to-ics-0.0.1-SNAPSHOT.jar" (
  echo [1/2] First run. Building jar, please wait...
  call mvn -q -DskipTests package
  if errorlevel 1 (
    echo Build failed. Please make sure JDK and Maven are installed and on PATH.
    pause
    exit /b 1
  )
)

echo Starting server. Browser will open automatically when ready...
echo This black window IS the running server. Close it to stop the service.
echo.

rem Wait for the port to be ready in background, then open the browser (max 40s)
start "" /min powershell -NoProfile -Command "for($i=0;$i -lt 40;$i++){try{$r=Invoke-WebRequest 'http://localhost:8080/api/v1/health' -UseBasicParsing -TimeoutSec 1;if($r.StatusCode -eq 200){Start-Process 'http://localhost:8080/';break}}catch{};Start-Sleep 1}"

rem Run server in foreground; logs show in this window
rem --enable-native-access silences a harmless Java 24 + Tomcat warning
java --enable-native-access=ALL-UNNAMED -jar "target\course-to-ics-0.0.1-SNAPSHOT.jar"

echo.
echo Server stopped.
pause
