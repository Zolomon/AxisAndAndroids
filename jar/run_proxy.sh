#!/bin/sh

# --------------------------------------------------------
# Start the proxies on: appropriate cameras and ports.
#
# Connect to each camera: 	telnet <host>
# Then run: 			/etc/CameraProxy <port>
# --------------------------------------------------------

java -jar CameraServer.jar 6004 -camera argus-7.student.lth.se 4444 &
java -jar CameraServer.jar 6006 -camera argus-8.student.lth.se 4446 &
java -jar CameraServer.jar 6002 -camera argus-5.student.lth.se 4321 &

java -jar DesktopClient.jar localhost 6004 localhost 6006 localhost 6002 &


