#!/bin/sh

cd server/bin
java -cp .:../lib/fakecamera.jar:../lib/media.jar se/axisandandroids/fakecamserver/FakeCameraServer 6000 &
java -cp .:../lib/fakecamera.jar:../lib/media.jar se/axisandandroids/fakecamserver/FakeCameraServer 6001 &
java -cp .:../lib/fakecamera.jar:../lib/media.jar se/axisandandroids/fakecamserver/FakeCameraServer 6002 &
java -cp .:../lib/fakecamera.jar:../lib/media.jar se/axisandandroids/fakecamserver/FakeCameraServer 6003 &
java -cp .:../lib/fakecamera.jar:../lib/media.jar se/axisandandroids/fakecamserver/FakeCameraServer 6004 &
java -cp .:../lib/fakecamera.jar:../lib/media.jar se/axisandandroids/fakecamserver/FakeCameraServer 6005 &
cd ../..

sleep 4

cd desktop-client/bin
java -cp .:../../server/bin/:../../client/bin/classes/ se/axisandandroids/desktop/client/DesktopClient localhost 6000 localhost 6001 localhost 6002 localhost 6003 localhost 6004 localhost 6005  &
cd ../..


