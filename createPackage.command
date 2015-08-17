#!/bin/bash
cd "$( dirname "$0" )"

echo Creating JAR
gradle desktop:dist || exit

echo Copying GameData
cp -r core/ZombieGameData desktop/build/libs/ZombieGameData || exit

echo Creating Zip file
cd desktop/build/libs || exit
zip -r -q ~/Desktop/ZombieGame.zip ./ || exit

echo Clearing Build Folder
rm -r *