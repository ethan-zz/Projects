if( POLICY CMP0020 )
	cmake_policy( SET CMP0020 NEW)
endif()
set( CMAKE_PREFIX_PATH "../../../../vtk/vtk_2013" )
find_package( VTK REQUIRED )
#message(STATUS VTK_USE_FILE=${VTK_USE_FILE})
get_filename_component(VTK_USE_FILE_DIR ${VTK_USE_FILE} DIRECTORY )
set(VTK_ROOT_DIR "${VTK_USE_FILE_DIR}/../.." )
#message(STATUS VTK_INCLUDE_DIRS=${VTK_INCLUDE_DIRS})
#IMPORTANT: Fix NULL Ptr issues
include( ${VTK_USE_FILE} )

set( CMAKE_PREFIX_PATH "../../../../Qt/Qt5.3.1/5.3/msvc2013_64_opengl" )
find_package( Qt5Widgets REQUIRED )
#find_package( Qt5Declarative )
#add_definitions(${Qt5Widgets_DEFINITIONS})
set(QT_LIBRARIES ${Qt5Widgets_LIBRARIES})

include_directories(
	${VTK_INCLUDE_DIRS}
	${CMAKE_CURRENT_SOURCE_DIR}/../Packages
	${Qt5Widgets_INCLUDE_DIRS}
)

get_target_property( Qt5Widgets_DLL_LOCATION ${Qt5Widgets_LIBRARIES} LOCATION )
get_filename_component( Qt5Widgets_DLL_dir ${Qt5Widgets_DLL_LOCATION} DIRECTORY )

set( MocHeaders
	vtkMainWindow.h
)
Qt5_WRAP_CPP( MocCpps ${MocHeaders} )

set( Headers
	${MocHeaders}
)
set( Srcs
	${Headers}
	${MocCpps}
	#	vtkMain3D.cpp
	#	vtkMainWindow.cpp
	vtkMainSurfacePlot.cpp
	vtkMainWindowSurfacePlot.cpp
)

#link_directories( "${Qt5Widgets_DLL_dir}/../lib")
add_executable( vtkExplorer ${Resource_Files} ${Srcs} )
add_dependencies( vtkExplorer
	QVTKWidget
)
target_link_libraries( vtkExplorer
	QVTKWidget
	${QT_LIBRARIES}
	${VTK_LIBRARIES}
	#	debug Qt5Cored.lib
	#	optimized Qt5Core.lib
	#	debug Qt5Guid.lib
	#	optimized Qt5Gui.lib
	#	debug Qt5Widgetsd.lib
	#	optimized Qt5Widgets.lib
	#	debug Debug/vtkRenderingCore-6.1
)


set( VtkDLLs 
	vtkalglib-6.1.dll
	vtkChartsCore-6.1.dll
	vtkCommonColor-6.1.dll
	vtkCommonComputationalGeometry-6.1.dll
	vtkCommonCore-6.1.dll
	vtkCommonDataModel-6.1.dll
	vtkCommonExecutionModel-6.1.dll
	vtkCommonMath-6.1.dll
	vtkCommonMisc-6.1.dll
	vtkCommonSystem-6.1.dll
	vtkCommonTransforms-6.1.dll
	vtkDICOMParser-6.1.dll
	vtkFiltersCore-6.1.dll
	vtkFiltersExtraction-6.1.dll
	vtkFiltersGeneral-6.1.dll
	vtkFiltersGeometry-6.1.dll
	vtkFiltersSources-6.1.dll
	vtkFiltersStatistics-6.1.dll
	vtkfreetype-6.1.dll
	vtkftgl-6.1.dll
	vtkImagingCore-6.1.dll
	vtkImagingFourier-6.1.dll
	vtkImagingHybrid-6.1.dll
	vtkInfovisCore-6.1.dll
	vtkInteractionStyle-6.1.dll
	vtkIOImage-6.1.dll
	vtkjpeg-6.1.dll
	vtkmetaio-6.1.dll
	vtkpng-6.1.dll
	vtkRenderingContext2D-6.1.dll
	vtkRenderingCore-6.1.dll
	vtkRenderingFreeType-6.1.dll
	vtkRenderingFreeTypeOpenGL-6.1.dll
	vtkRenderingOpenGL-6.1.dll
	vtkRenderingVolume-6.1.dll
	vtkRenderingVolumeOpenGL-6.1.dll
	vtksys-6.1.dll
	vtktiff-6.1.dll
	vtkViewsContext2D-6.1.dll
	vtkViewsCore-6.1.dll
	vtkzlib-6.1.dll
)
foreach( VtkDll ${VtkDLLs} ) 
	add_custom_command( TARGET vtkExplorer POST_BUILD
		COMMAND ${CMAKE_COMMAND} -E copy_if_different
		"${VTK_ROOT_DIR}/vtk_2013/bin/$<CONFIGURATION>/${VtkDll}"
		$<TARGET_FILE_DIR:vtkExplorer> )
endforeach( VtkDll )

