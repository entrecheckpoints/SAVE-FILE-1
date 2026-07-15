@echo off
cd /d "%~dp0\.."
call gradlew.bat testDebugUnitTest assembleDebug --stacktrace
if errorlevel 1 exit /b %errorlevel%
echo.
echo APK: app\build\outputs\apk\debug\app-debug.apk
