// ------ Copyright -----
// by Zhengrong Zhou 2014
// ----------------------
#pragma comment(linker, "/SUBSYSTEM:windows /ENTRY:mainCRTStartup")

#include "MainWindow.h"
#include <QtWidgets/QApplication>

int main(int argc, char * argv[])
{
	QApplication app(argc, argv);
	zz::MainWindow & mainWin = zz::MainWindow::instance();
	mainWin.show();
	return app.exec();
}



