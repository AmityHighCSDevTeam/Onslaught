#!/bin/bash
cd "$( dirname "$0" )"

echo Deleting Extraneous Files
rm core/OnslaughtData/log.log
rm core/OnslaughtData/settings.json
rm -r core/OnslaughtData/saves

echo Deleting Previous Builds
rm ~/Desktop/Onslaught.zip

echo Creating JAR and Incrementing Build Number
gradle desktop:dist incrementBuildNum || exit

echo Copying GameData
cp -r core/OnslaughtData desktop/build/libs/OnslaughtData || exit

echo Creating Zip file
cd desktop/build/libs || exit
zip -r -q ~/Desktop/Onslaught.zip ./ -x "*.DS_Store" || exit

echo Clearing Build Folder
rm -r *
