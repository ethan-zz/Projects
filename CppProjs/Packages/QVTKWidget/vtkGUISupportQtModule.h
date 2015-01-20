// ------ Copyright -----
// by Zhengrong Zhou
// ----------------------

#pragma once

#ifdef WIN32
#ifdef EXPORT_VTKGUISUPPORTQT
#define VTKGUISUPPORTQT_EXPORT __declspec( dllexport )
#else
#define VTKGUISUPPORTQT_EXPORT __declspec( dllimport )
#endif
#else
#define VTKGUISUPPORTQT_EXPORT
#endif

