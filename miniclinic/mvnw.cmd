@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF)
@REM under the Apache License, Version 2.0.
@REM ----------------------------------------------------------------------------

@IF "%__MVNW_ARG0_NAME__%"=="" (SET __MVNW_ARG0_NAME__=%~nx0)
@SET __MVNW_CMD__=
@SET __MVNW_ERROR__=
@SET __MVNW_SAVE_ERRORLEVEL__=
@SET __MVNW_SAVE_ERRORLEVEL__=%ERRORLEVEL%

@REM Find the project base dir, i.e. the directory that contains the folder ".mvn".
@REM Fallback to current directory if not found.

@SET __MVNW_PROJECT_BASEDIR__=%~dp0

@SET __MVNW_WRAPPER_DIR__=%__MVNW_PROJECT_BASEDIR__%.mvn\wrapper

@SET __MVNW_WRAPPER_PROPERTIES__=%__MVNW_WRAPPER_DIR__%\maven-wrapper.properties

@SET __MVNW_DISTRIBUTION_URL__=
@FOR /F "tokens=2 delims==" %%i IN ('findstr "distributionUrl" "%__MVNW_WRAPPER_PROPERTIES___%"') DO (
  @SET __MVNW_DISTRIBUTION_URL__=%%i
)

@SET __MVNW_FILENAME__=
@FOR %%i IN ("%__MVNW_DISTRIBUTION_URL__%") DO @SET __MVNW_FILENAME__=%%~ni

@SET __MVNW_DIST_CACHE__=%USERPROFILE%\.m2\wrapper\dists\%__MVNW_FILENAME__%

@IF NOT EXIST "%__MVNW_DIST_CACHE__%" (
  @MKDIR "%__MVNW_DIST_CACHE__%" 2>NUL
)

@SET __MVNW_MAVEN_EXEC__=
@FOR /D %%d IN ("%__MVNW_DIST_CACHE__%\*") DO (
  @IF EXIST "%%d\bin\mvn.cmd" @SET __MVNW_MAVEN_EXEC__=%%d\bin\mvn.cmd
)

@IF "%__MVNW_MAVEN_EXEC__%"=="" (
  @echo Downloading Maven from %__MVNW_DISTRIBUTION_URL__%...
  @powershell -Command "$url='%__MVNW_DISTRIBUTION_URL__%'; $dest='%__MVNW_DIST_CACHE__%\%__MVNW_FILENAME__%'.zip'; Invoke-WebRequest -Uri $url -OutFile $dest; Expand-Archive -Path $dest -DestinationPath '%__MVNW_DIST_CACHE__%' -Force; Remove-Item $dest"
  @FOR /D %%d IN ("%__MVNW_DIST_CACHE__%\*") DO (
    @IF EXIST "%%d\bin\mvn.cmd" @SET __MVNW_MAVEN_EXEC__=%%d\bin\mvn.cmd
  )
)

@IF "%__MVNW_MAVEN_EXEC__%"=="" (
  @echo ERROR: Maven could not be downloaded or found.
  @EXIT /B 1
)

@SET JAVA_HOME_BAK=%JAVA_HOME%
@IF "%JAVA_HOME%"=="" (
  @FOR /F "tokens=*" %%i IN ('where java 2^>NUL') DO @SET __MVNW_JAVA__=%%i
) ELSE (
  @SET __MVNW_JAVA__=%JAVA_HOME%\bin\java.exe
)

@"%__MVNW_MAVEN_EXEC__%" %*
