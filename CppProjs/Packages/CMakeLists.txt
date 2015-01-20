#add_subdirectory( Algorithms )
add_subdirectory( Variant )
add_subdirectory( QVTKWidget )
add_subdirectory( QVTKOpenGL )

#set_property( TARGET Algorithms PROPERTY FOLDER "Static Libs" )
set_property( TARGET Variant PROPERTY FOLDER "Shared Libs" )
set_property( TARGET QVTKWidget PROPERTY FOLDER "Shared Libs Ext/QVTK" )
set_property( TARGET QVTKOpenGL PROPERTY FOLDER "Shared Libs Ext/QVTK" )

