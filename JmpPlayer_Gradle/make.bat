call .\gradlew.bat :jmpp:fatJar

mkdir JMPPlayer_vX.XX

copy .\jmpp\build\libs\JMPPlayer-1.0.jar .\JMPPlayer_vX.XX\JMPPlayer.jar
xcopy .\jmpp\bin\skin .\JMPPlayer_vX.XX\skin /S /E /I /H /Y
copy .\jmpp\bin\*.exe .\JMPPlayer_vX.XX

pause