#!/bin/sh

cd server/bin
java -cp .:../lib/cameraproxy.jar se/axisandandroids/server/CameraServer 6004 -camera argus-7.student.lth.se 4444 &
java -cp .:../lib/cameraproxy.jar se/axisandandroids/server/CameraServer 6006 -camera argus-8.student.lth.se 4446 &
java -cp .:../lib/cameraproxy.jar se/axisandandroids/server/CameraServer 6002 -camera argus-5.student.lth.se 4321 &
#java -cp .:../lib/fakecamera.jar:../lib/media.jar se/axisandandroids/fakecamserver/FakeCameraServer 6003 &
cd ../..

sleep 4

cd desktop-client/bin
java -cp .:../../server/bin/:../../client/bin/classes/ se/axisandandroids/desktop/client/DesktopClient localhost 6004 localhost 6006 localhost 6002 #localhost 6003 &
cd ../..


