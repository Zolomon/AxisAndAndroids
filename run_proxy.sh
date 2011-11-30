#!/bin/sh

#
# Proxies have to be running on correct ports, etc. BEFORE this
# script is invoked.
# 
# Start each proxies by:
# 		telnet:ing into the cameras and run: /etc/CameraProxy <port>
#
# Time seem to be off by one hour in the cameras so remove two comments
# in DesktopDisplayThread.java in the display-client project.
#
# The TWO rows to uncomment look like this:
# //timestamp = timestamp - 3600000; // ------------------------------------------> GMT+1
#


# Start the camera servers
cd server/bin
java -cp .:../lib/cameraproxy.jar se/axisandandroids/server/CameraServer 6004 -camera argus-7.student.lth.se 4444 &
java -cp .:../lib/cameraproxy.jar se/axisandandroids/server/CameraServer 6006 -camera argus-8.student.lth.se 4446 &
java -cp .:../lib/cameraproxy.jar se/axisandandroids/server/CameraServer 6002 -camera argus-5.student.lth.se 4321 &
cd ../..

# To ensure server are initialized before client tries to connect.
sleep 4


# Start the desktop client
cd desktop-client/bin
java -cp .:../../server/bin/:../../client/bin/classes/ se/axisandandroids/desktop/client/DesktopClient localhost 6004 localhost 6006 localhost 6002 
cd ../..


