#message( STATUS CMAKE_PREFIX_PATH= ${CMAKE_PREFIX_PATH} ) 
set( CMAKE_PREFIX_PATH "../../../../opencv/build" )
find_package( OpenCV REQUIRED)
message( STATUS OpenCV_INCLUDE_DIRS= ${OpenCV_INCLUDE_DIRS} )
message( STATUS OpenCV_LIB_PATH= ${OpenCV_LIB_PATH} )
message( STATUS OpenCV_LIB_COMPONENTS= ${OpenCV_LIB_COMPONENTS} )

set( CMAKE_PREFIX_PATH "../../../../Qt/Qt5.3.1/5.3/msvc2013_64_opengl" )
find_package( Qt5Widgets REQUIRED )

include_directories(
	${CMAKE_CURRENT_SOURCE_DIR}
	${OpenCV_INCLUDE_DIRS}
	${Qt5Widgets_INCLUDE_DIRS}
)
get_target_property( Qt5Widgets_DLL_LOCATION ${Qt5Widgets_LIBRARIES} LOCATION )
get_filename_component( Qt5Widgets_DLL_dir ${Qt5Widgets_DLL_LOCATION} DIRECTORY )

set( MainUIQrc resources/MainUI.qrc )
set( AppIcons
	resources/Icons/add.gif
	resources/Icons/open_new.gif
	resources/Icons/save_edit.gif
	resources/Icons/saveas_edit.gif
	resources/Icons/print_edit.gif
	resources/Icons/import_wiz.gif
	resources/Icons/export_wiz.gif
	resources/Icons/file.gif
	resources/Icons/fldr_obj.gif
	resources/Icons/copy_edit.gif
	resources/Icons/cut_edit.gif
	resources/Icons/paste_edit.gif
	resources/Icons/run_exc.gif
	resources/Icons/stop.gif
	resources/Icons/build_exec.png
	resources/Icons/icon_show_callees.png
	resources/Icons/icon_db_edit_src_lookup.png
	resources/Icons/outlineView-Sort.png
	resources/Icons/stash.png
)
source_group( "Resource Files" FILES ${MainUIQrc} )
source_group( "Resource Files\\Icons" FILES ${AppIcons} )
set( Resource_Files ${AppIcons} )
#set_source_files_properties( ${MFiles} PROPERTIES LANGUAGE NONE )
qt5_add_resources( MainUIQrcCpp ${MainUIQrc} )

set( MocDockerHeaders
	dockers/ComponentTree1.hxx
	dockers/ComponentTree2.hxx
	dockers/DockerManager.hxx
	dockers/Log.hxx
	dockers/ProjectsTree.hxx
)
set( DockerSrcs
	${MocDockerHeaders}
	dockers/ComponentTree1.cpp
	dockers/ComponentTree2.cpp
	dockers/DockerManager.cpp
	dockers/Log.cpp
	dockers/ProjectsTree.cpp
)
source_group( "Dockers" FILES ${DockerSrcs} )

set( MocMenuHeaders
	menus/MenuAction.hxx
	menus/MenuEdit.hxx
	menus/MenuFile.hxx
	menus/MenuView.hxx
)
set( MenuSrcs
	${MocMenuHeaders}
	menus/MainMenus.h
	menus/MenuHelp.h
	menus/MainMenus.cpp
	menus/MenuAction.cpp
	menus/MenuEdit.cpp
	menus/MenuFile.cpp
	menus/MenuHelp.cpp
	menus/MenuView.cpp
)
source_group( "MenusToobars" FILES ${MenuSrcs} )

set( UtilitySrcs
	utilities/ImageUtility.h
	utilities/ImageUtility.cpp
	utilities/StringUtility.h
	utilities/StringUtility.cpp
)
source_group( "Utility Files" FILES ${UtilitySrcs} )

set( MocMainHeaders
	editors/ImageEditor.hxx
	MainWindow.hxx
)
set( MainSrcs
	${MocMainHeaders}
	editors/ImageEditor.cpp
	MainUIApp.cpp
	MainWindow.cpp
)

set( MocHeaders
	${MocMainHeaders}
	${MocDockerHeaders}
	${MocMenuHeaders}
)
Qt5_WRAP_CPP( MocCpps ${MocHeaders} )
source_group( "Qt Generated Files" FILES ${MocCpps} ${MainUIQrcCpp})

set( Srcs
	${MainUIQrcCpp}
	${MainSrcs}
	${MocCpps}
	${DockerSrcs}
	${MenuSrcs}
	${UtilitySrcs}
)

link_directories( "${Qt5Widgets_DLL_dir}/../lib" ${OpenCV_LIB_PATH} )
add_executable( MainUIApp WIN32 ${Resource_Files} ${Srcs} )
target_link_libraries( MainUIApp
	debug Qt5Cored.lib
	optimized /Qt5Core.lib
	debug Qt5Guid.lib
	optimized Qt5Gui.lib
	debug Qt5Widgetsd.lib
	optimized Qt5Widgets.lib
	debug opencv_core2410d.lib
	optimized opencv_core2410.lib
	debug opencv_highgui2410d.lib
	optimized opencv_highgui2410.lib
)

set( QtDLLs 
	Qt5Core.dll
	Qt5Cored.dll
	Qt5Gui.dll
	Qt5Guid.dll
	Qt5Widgets.dll
	Qt5Widgetsd.dll
	icuin52.dll
	icudt52.dll
	icuuc52.dll
)
message( STATUS Qt5Widgets_DLL_dir= ${Qt5Widgets_DLL_dir})
foreach( QtDll ${QtDLLs} ) 
	add_custom_command( TARGET MainUIApp POST_BUILD
		COMMAND ${CMAKE_COMMAND} -E copy_if_different
		"${Qt5Widgets_DLL_dir}/${QtDll}"
		$<TARGET_FILE_DIR:MainUIApp> )
endforeach( QtDll )

set( OpenCVDLLs
	opencv_core2410.dll
	opencv_core2410d.dll
	opencv_highgui2410.dll
	opencv_highgui2410d.dll
)
foreach( OpenCVDLL ${OpenCVDLLs} ) 
	add_custom_command( TARGET MainUIApp POST_BUILD
		COMMAND ${CMAKE_COMMAND} -E copy_if_different
		"${OpenCV_LIB_PATH}/../bin/${OpenCVDLL}"
		$<TARGET_FILE_DIR:MainUIApp> )
endforeach( OpenCVDLL )

