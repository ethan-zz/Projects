set dirname=ZExplore_VS2013

cd ../../
if not exist %dirname% (
mkdir %dirname%
)

cd %dirname%
cmake -G "Visual Studio 12 2013 Win64" ../MyProjs/CppProjs

pause
