// ------ Copyright -----
// by Zhengrong Zhou 2015
// ----------------------

#include "MenuHelp.h"

#include <QtWidgets/qmenu.h>

MenuHelp & MenuHelp::inst()
{
	static MenuHelp s_me;
	return s_me;
}

MenuHelp::MenuHelp(void)
{
}

MenuHelp::~MenuHelp(void)
{
}

void MenuHelp::populate(QMenu * menu, QToolBar * toolbar)
{
}
