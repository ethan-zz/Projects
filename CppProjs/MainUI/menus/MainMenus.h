// ------ Copyright -----
// by Zhengrong Zhou 2015
// ----------------------

#pragma once

#include <QtCore/qglobal.h> // For Q_DISABLE_COPY

class QMainWindow;

class MainMenus
{
public:
	static MainMenus & inst();
	~MainMenus(void);

	void populateMenubar(QMainWindow * mainwin);

private:
	MainMenus(void);
	Q_DISABLE_COPY(MainMenus);
};

