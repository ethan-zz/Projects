set( CMAKE_PREFIX_PATH "../../../../../Qt/Qt5.3.1/5.3/msvc2013_64_opengl" )
find_package(Qt5OpenGL REQUIRED QUIET)

set( CMAKE_PREFIX_PATH "../../../../../vtk/vtk_2013" )
find_package( VTK REQUIRED )

include_directories(
	${CMAKE_CURRENT_SOURCE_DIR}/../QVTKWidget
	${Qt5OpenGL_INCLUDE_DIRS}
	${VTK_INCLUDE_DIRS} 
)

set(MocHeaders
  QVTKGraphicsItem.h
  QVTKWidget2.h
  )
set(LibSrcs ${QVTKLibSrcs}
  vtkGUISupportQtOpenGLModule.h
  QVTKGraphicsItem.cxx
  QVTKWidget2.cxx
  )

add_definitions( -DEXPORT_VTKGUISUPPORTQTOPENGL ${Qt5OpenGL_DEFINITIONS})
qt5_wrap_cpp(LibMocSrcs ${MocHeaders})

set(QT_LIBRARIES ${Qt5OpenGL_LIBRARIES})

add_library(QVTKOpenGL SHARED ${MocHeaders} ${LibSrcs} ${LibMocSrcs})
add_dependencies( QVTKOpenGL
	QVTKWidget
)
target_link_libraries(QVTKOpenGL QVTKWidget ${QT_LIBRARIES})

