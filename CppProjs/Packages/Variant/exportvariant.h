// ------ Copyright -----
// by Zhengrong Zhou
// ----------------------

#pragma once

#ifdef WIN32
#ifdef EXPORT_VARIANT
#define VARIANT_API __declspec( dllexport )
#else
#define VARIANT_API __declspec( dllimport )
#endif
#else
#define VARIANT_API
#endif

