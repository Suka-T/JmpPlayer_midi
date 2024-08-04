@echo off
set /p ANS="Initialize to the state at the time of installation.(Y/N)?"

if /i {%ANS%}=={y} (goto :yes)
if /i {%ANS%}=={yes} (goto :yes)

EXIT

:yes
rd /s /q plugins
rd /s /q jmz
rd /s /q save
rd /s /q output
rem clean.bat

pause