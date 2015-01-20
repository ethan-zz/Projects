Explore 3D graphics with Visualization Toolkit (VTK)
x64 executible is built and tested with Visual Studio Express 2013 for Windows

1. Required software
a) Visual Studio Express 2013 for Windows
b) CMake 3.0.2
c) qt-opensource-windows-x86-msvc2013_64_opengl-5.3.1
d) VTK-6.1.0 (Follow VTK's instruction to build with Visual Studio Express 2013 for Windows)

2. To build
If everything follows the directory structure described here, just run
create_vs2013_solution.bat and a VS2013 solution "ZExplore.sln" will be created under ZExplore_VS2013 directory as shown below.

3. Directory structure: () denote content created by create_vs2013_solution.bat


top_dir -- sub_dir -- sub_dir -- CppProjs -- create_vs2013_solution.bat
       |          |                      |-- README.txt
       |          |                      |-- ...
       |          |
       |          |-- (ZExplore_VS2013) -- (ZExplore.sln)
       |                               |-- (...)
       | 
       |-- vtk -- vtk_2013 -- bin -- Debug -- *.dll
       |      |
       |      |-- VTK-6.1.0 -- unzipped VTK content
       |
       |-- QT -- Qt5.3.1 -- Qt content
   





