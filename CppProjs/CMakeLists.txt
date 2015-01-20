cmake_minimum_required(VERSION 3.0.2)

project( ZExplore )

set_property( GLOBAL PROPERTY USE_FOLDERS ON )

set( CMAKE_RUNTIME_OUTPUT_DIRECTORY ${PROJECT_BINARY_DIR} )
add_subdirectory( ZExplorer )
add_subdirectory( vtkExplorer )
set_property( TARGET ZExplore PROPERTY FOLDER "Applications" )
set_property( TARGET vtkExplorer PROPERTY FOLDER "Applications" )

add_subdirectory( Packages ) 


