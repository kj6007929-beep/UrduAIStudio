\
@echo off
setlocal
cd /d "%~dp0"
where java >nul 2>nul || (echo ERROR: Install JDK 17 first.& exit /b 1)
if "%ANDROID_HOME%"=="" if "%ANDROID_SDK_ROOT%"=="" (echo ERROR: Set ANDROID_HOME or ANDROID_SDK_ROOT to your Android SDK path.& exit /b 1)
where gradle >nul 2>nul || (echo ERROR: Gradle is not installed. Open this folder in Android Studio and build using its Gradle sync.& exit /b 1)
gradle --no-daemon assembleDebug
if errorlevel 1 exit /b %errorlevel%
echo APK output: app\build\outputs\apk\debug\app-debug.apk
