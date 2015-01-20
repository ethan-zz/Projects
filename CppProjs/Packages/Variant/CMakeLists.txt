set ( Srcs
	Variant.h
	Variant.cpp
	)

add_definitions( -DEXPORT_VARIANT )
add_library( Variant SHARED ${Srcs} )

