# Windows bat

echo "OSX App bundler running..."

set DIR = %~dp0

echo "Setting working dir to %DIR%!"
cd %DIR%

echo "Copy Vidada.app template to target"
xcopy Vidada.app .\..\target\Vidada.app /E
cd .\..\target\

echo "Assembling Vidada.jar into app..."
cp Vidada.jar Vidada.app\Contents\Resources\Java\Vidada.jar

echo "OSX App created successfully!"
