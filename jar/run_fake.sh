#!/bin/sh
java -jar FakeCameraServer.jar 6000 &
java -jar FakeCameraServer.jar 6001 &
sleep 3
java -jar DesktopClient_standard.jar localhost 6000 localhost 6001 &
