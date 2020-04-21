@echo off

if defined VS140COMNTOOLS (
	if not exist "%VS140COMNTOOLS%vcvarsqueryregistry.bat" goto NoVS
	call "%VS140COMNTOOLS%vcvarsqueryregistry.bat"
	goto :run
)

:NoVS
echo Error: Visual Studio 2015 required.
exit /b 1

:run
echo %UniversalCRTSdkDir%
echo %UCRTVersion%
