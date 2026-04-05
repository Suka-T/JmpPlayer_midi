call .\gradlew.bat jar

mkdir libs

copy .\ffmpegw\build\libs\*.jar .\libs
copy .\flib\build\libs\*.jar .\libs
copy .\jmplib\build\libs\*.jar .\libs
copy .\jmpp\build\libs\*.jar .\libs
copy .\jmsynth\build\libs\*.jar .\libs
copy .\mkjmp\build\libs\*.jar .\libs

pause