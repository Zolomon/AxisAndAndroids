#!/bin/sh
java -jar FakeCameraServer.jar &
sleep 3
java -jar DesktopClient_standard.jar localhost 6000 localhost 6000 &
