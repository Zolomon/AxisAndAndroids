#!/bin/sh
java -jar FakeCameraServer.jar &
sleep 2
java -jar DesktopClient_standard.jar localhost 6000 localhost 6000 &
