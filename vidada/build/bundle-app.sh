#!/bin/sh

echo "OSX App bundler running..."


# Get the directory where this script is in (usually ./build)
SOURCE="${BASH_SOURCE[0]}"
DIR="$( dirname "$SOURCE" )"
while [ -h "$SOURCE" ]
do 
  SOURCE="$(readlink "$SOURCE")"
  [[ $SOURCE != /* ]] && SOURCE="$DIR/$SOURCE"
  DIR="$( cd -P "$( dirname "$SOURCE"  )" && pwd )"
done
DIR="$( cd -P "$( dirname "$SOURCE" )" && pwd )"

cd $DIR

echo "Copy Vidada.app template to target"
cp -r Vidada.app ./../target/Vidada.app
cd ./../target/

echo "Assembling Vidada.jar into app..."
cp Vidada.jar Vidada.app/Contents/Resources/Java/Vidada.jar

echo "OSX App created successfully!"
