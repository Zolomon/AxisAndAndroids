#!/bin/sh

# --------------------------------------------------------
# Start the proxies on: appropriate cameras and ports.
#
# Connect to each camera: 	telnet <host>
# Then run: 			/etc/CameraProxy <port>
# --------------------------------------------------------

java -jar CameraServer.jar 6004 -camera argus-7.student.lth.se 4444 &
java -jar CameraServer.jar 6006 -camera argus-8.student.lth.se 4446 &

sleep 4

java -jar  DesktopClient_onehourplus.jar localhost 6004 localhost 6006 &


