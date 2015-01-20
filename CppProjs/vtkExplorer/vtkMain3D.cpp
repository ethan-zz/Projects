// ------ Copyright -----
// by Zhengrong Zhou 2014
// ----------------------
#pragma comment(linker, "/SUBSYSTEM:windows /ENTRY:mainCRTStartup")

#include "vtkMainWindow.h"
#include <QtWidgets/QApplication>

int main(int argc, char * argv[])
{
	QApplication app(argc, argv);
	zz::vtkMainWindow & mainWin = zz::vtkMainWindow::instance();
	mainWin.show();
	return app.exec();
}



