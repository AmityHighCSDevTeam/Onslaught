#!/bin/bash
cd "$( dirname "$0" )"

echo Creating JAR
gradle desktop:dist

echo Copying GameData
cp -r core/ZombieGameData desktop/build/libs/ZombieGameData

echo Creating Zip file
cd desktop/build/libs
zip -r -q ~/Desktop/ZombieGame.zip ./

echo Clearing Build Folder
rm -r *