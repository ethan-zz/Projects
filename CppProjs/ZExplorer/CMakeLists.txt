set( CMAKE_PREFIX_PATH "../../../../Qt/Qt5.3.1/5.3/msvc2013_64_opengl" )

find_package( Qt5Widgets REQUIRED )
#find_package( Qt5Declarative )

include_directories(
	${CMAKE_CURRENT_SOURCE_DIR}/../Packages
	${Qt5Widgets_INCLUDE_DIRS}
)
get_target_property( Qt5Widgets_DLL_LOCATION ${Qt5Widgets_LIBRARIES} LOCATION )
get_filename_component( Qt5Widgets_DLL_dir ${Qt5Widgets_DLL_LOCATION} DIRECTORY )

set( AppIcons
	../../Icons/import48.png
	../../Icons/open16.png
	../../Icons/new_icon16.png
	../../Icons/ZExplore.png
)
source_group( "Resource Files\\Icons" FILES ${AppIcons} )
set( Resource_Files
	${AppIcons}
)
#set_source_files_properties( ${MFiles} PROPERTIES LANGUAGE NONE )
qt5_add_resources( ZExploreQrc ZExplore.qrc )

set( MocHeaders
	MainWindow.h
	FileMenuManager.h
	WorkspaceTree.h
)
Qt5_WRAP_CPP( MocCpps ${MocHeaders} )

set( Headers
	${MocHeaders}
)
set( Srcs
	${ZExploreQrc}
	${Headers}
	${MocCpps}
	FileMenuManager.cpp
	main.cpp
	MainWindow.cpp
	WorkspaceTree.cpp
)

link_directories( "${Qt5Widgets_DLL_dir}/../lib" )
add_executable( ZExplore ${Resource_Files} ${Srcs} )
target_link_libraries( ZExplore
	Variant
	debug Qt5Cored.lib
	optimized /Qt5Core.lib
	debug Qt5Guid.lib
	optimized Qt5Gui.lib
	debug Qt5Widgetsd.lib
	optimized Qt5Widgets.lib
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
	add_custom_command( TARGET ZExplore POST_BUILD
		COMMAND ${CMAKE_COMMAND} -E copy_if_different
		"${Qt5Widgets_DLL_dir}/${QtDll}"
		$<TARGET_FILE_DIR:ZExplore> )
endforeach( QtDll )

