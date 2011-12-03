#!/bin/sh
java -jar FakeCameraServer.jar 6000 &
java -jar FakeCameraServer.jar 6001 &
java -jar FakeCameraServer.jar 6002 &
java -jar FakeCameraServer.jar 6003 &
java -jar FakeCameraServer.jar 6004 &
java -jar FakeCameraServer.jar 6005 &
sleep 4
java -jar DesktopClient_standard.jar localhost 6000 localhost 6001 localhost 6002 localhost 6003 localhost 6004 localhost 6005 &
