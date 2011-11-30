#!/bin/sh
java -jar FakeCameraServer.jar &
java -jar DesktopClient.jar localhost 6000 localhost 6000 &
