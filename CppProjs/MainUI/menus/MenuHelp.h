// ------ Copyright -----
// by Zhengrong Zhou 2015
// ----------------------

#pragma once

#include <QtCore/qglobal.h> // For Q_DISABLE_COPY

class QMenu;
class QToolBar;

class MenuHelp
{
public:
	static MenuHelp & inst();
	~MenuHelp(void);

	void populate(QMenu * menu, QToolBar * toolbar);
private:
	MenuHelp(void);
	Q_DISABLE_COPY(MenuHelp)
};

