set( CMAKE_PREFIX_PATH "../../../../Qt/Qt5.3.1/5.3/msvc2013_64_opengl" )
find_package(Qt5Widgets REQUIRED QUIET)
set(QT_LIBRARIES ${Qt5Widgets_LIBRARIES})

if( POLICY CMP0020 )
	cmake_policy( SET CMP0020 NEW)
endif()
set( CMAKE_PREFIX_PATH "../../../../vtk/vtk_2013" )
find_package( VTK REQUIRED )

include_directories(${VTK_INCLUDE_DIRS} ${Qt5Widgets_INCLUDE_DIRS})

add_definitions( -DEXPORT_VTKGUISUPPORTQT )
set(QVTKLibSrcs
  vtkGUISupportQtModule.h
  vtkEventQtSlotConnect.cxx
  vtkQtConnection.cxx
  QVTKApplication.cxx
  QVTKInteractor.cxx
  QVTKInteractorAdapter.cxx
  QVTKWidget.cxx
  QFilterTreeProxyModel.cxx
  QVTKPaintEngine.cxx
  QVTKPaintEngine.h
  vtkQtAbstractModelAdapter.cxx
  vtkQtAnnotationLayersModelAdapter.cxx
  vtkQtDebugLeaksModel.cxx
  vtkQtDebugLeaksView.cxx
  vtkQtTableModelAdapter.cxx
  vtkQtTreeModelAdapter.cxx
  )

set(QVTKMocHeaders
  QVTKApplication.h
  QVTKInteractorAdapter.h
  QVTKInteractorInternal.h
  QVTKWidget.h
  vtkQtConnection.h
  QFilterTreeProxyModel.h
  vtkQtAbstractModelAdapter.h
  vtkQtAnnotationLayersModelAdapter.h
  vtkQtDebugLeaksModel.h
  vtkQtDebugLeaksView.h
  vtkQtTableModelAdapter.h
  vtkQtTreeModelAdapter.h
  )

qt5_wrap_cpp(QVTKLibMocSrcs ${QVTKMocHeaders})

#if(VTK_USE_X AND VTK_USE_TDX)
#  set(QVTKMocHeaders ${QVTKMocHeaders}
#    vtkTDxQtUnixDevices.h
#    )
#  set(QVTKLibSrcs ${QVTKLibSrcs}
#    vtkTDxQtUnixDevices.cxx
#    )
#endif()

set(QVTKNonMocHeaders
  QVTKWin32Header.h
  vtkEventQtSlotConnect.h
  QVTKInteractor.h
  )

add_library(QVTKWidget
  SHARED
  ${QVTKLibSrcs}
  ${QVTKNonMocHeaders}
  ${QVTKMocHeaders}
  ${QVTKLibMocSrcs}
)

target_link_libraries( QVTKWidget ${VTK_LIBRARIES} ${QT_LIBRARIES} )

