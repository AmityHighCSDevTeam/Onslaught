#!/bin/bash
cd "$( dirname "$0" )"

echo Deleting Extraneous Files
rm core/ZombieGameData/log.log
rm core/ZombieGameData/settings.json
rm -r core/ZombieGameData/saves

echo Deleting Previous Builds
rm ~/Desktop/ZombieGame.zip

echo Creating JAR and Incrementing Build Number
gradle desktop:dist incrementBuildNum || exit

echo Copying GameData
cp -r core/ZombieGameData desktop/build/libs/ZombieGameData || exit

echo Creating Zip file
cd desktop/build/libs || exit
zip -r -q ~/Desktop/ZombieGame.zip ./ -x "*.DS_Store" || exit

echo Clearing Build Folder
rm -r *
