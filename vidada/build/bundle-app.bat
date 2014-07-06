Echo "Windows OSX App bundler running..."

set DIR = %~dp0

Echo "Setting working dir to %DIR%!"
cd %DIR%

Echo "Copy Vidada.app template to target"
Xcopy Vidada.app .\..\target\Vidada.app /E /i /Y
cd .\..\target\

Echo "Assembling Vidada.jar into app..."
mkdir Vidada.app\Contents\Resources\Java
Copy vidada.jar Vidada.app\Contents\Resources\Java\Vidada.jar /Y

Echo "OSX App created successfully!"
