call .\gradlew.bat :jmpp:fatJar

mkdir JMPPlayer_vX.XX

copy .\jmpp\build\libs\JMPPlayer-1.0.jar .\JMPPlayer_vX.XX\JMPPlayer.jar
xcopy ..\jre .\JMPPlayer_vX.XX\jre /S /E /I /H /Y
xcopy ..\skin .\JMPPlayer_vX.XX\skin /S /E /I /H /Y
copy ..\clean.bat .\JMPPlayer_vX.XX
copy ..\clean_all.bat .\JMPPlayer_vX.XX
copy ..\JMPPlayer.bat .\JMPPlayer_vX.XX
copy ..\JMPPlayer_Unload_plugin.bat .\JMPPlayer_vX.XX
copy ..\JMPPlayer.exe .\JMPPlayer_vX.XX
copy ..\ffmpeg.exe .\JMPPlayer_vX.XX
copy ..\ffprobe.exe .\JMPPlayer_vX.XX

pause