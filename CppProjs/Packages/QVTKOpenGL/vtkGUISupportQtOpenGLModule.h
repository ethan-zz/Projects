// ------ Copyright -----
// by Zhengrong Zhou
// ----------------------

#pragma once

#ifdef WIN32
#ifdef EXPORT_VTKGUISUPPORTQTOPENGL
#define VTKGUISUPPORTQTOPENGL_EXPORT __declspec( dllexport )
#else
#define VTKGUISUPPORTQTOPENGL_EXPORT __declspec( dllimport )
#endif
#else
#define VTKGUISUPPORTQTOPENGL_EXPORT
#endif

