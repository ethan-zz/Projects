// ------ Copyright -----
// by Zhengrong Zhou 2015
// ----------------------

// MainUI.cpp : Defines the entry point for the application.
//

#include <Windows.h>
#include <string>
#include <QtWidgets/QApplication>
#include "MainWindow.hxx"

//int _tWinMain(int argc, char* argv[])
//{
//	QApplication app( argc, argv );
//
//	MainWindow mainwin;
//	mainwin.show();
//	
//	return app.exec();
//}

MainWindow * g_appMainWindow = nullptr;

int WINAPI WinMain(HINSTANCE, HINSTANCE, LPSTR lpCmdLine, int)
{
	char **argv = NULL;
	int argc = 0;

	if (*lpCmdLine != '\x0')
	{
		char *cmdLineCopy = new char[strlen(lpCmdLine) + 1];
		strcpy_s(cmdLineCopy, strlen(lpCmdLine) + 1, lpCmdLine);

		char *c = cmdLineCopy;
		while (c)
		{
			++argc;
			c = strchr((c + 1), ' ');
		}

		argv = new char *[argc];

		if (argc > 0)
		{
			argv[argc] = cmdLineCopy;
			char *c = strchr(cmdLineCopy, ' ');
			int n = 2;
			while (c)
			{
				*c = '\x0';
				argv[n] = (c + 1);
				++n;
				c = strchr((c + 1), ' ');
			}
		}
	}

	QApplication app(argc, argv);

	//QString dir = QApplication::applicationDirPath();
	MainWindow mainwin;
	g_appMainWindow = &mainwin;
	mainwin.initialize();
	mainwin.restoreApplicationSettings();
	mainwin.show();
	int ret = app.exec();
	mainwin.saveApplicationSettings();

	return ret;
}
