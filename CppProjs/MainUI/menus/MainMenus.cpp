// ------ Copyright -----
// by Zhengrong Zhou 2015
// ----------------------

#include "MainMenus.h"
#include "MenuFile.hxx"
#include "MenuEdit.hxx"
#include "MenuView.hxx"
#include "MenuAction.hxx"
#include "MenuHelp.h"

#include <QtWidgets/qmenubar.h>
#include <QtWidgets/qmainwindow.h>

MainMenus & MainMenus::inst()
{
	static MainMenus s_me;
	return s_me;
}

MainMenus::MainMenus(void)
{
}

MainMenus::~MainMenus(void)
{
}

void MainMenus::populateMenubar(QMainWindow * mainwin)
{
	QMenuBar * menuBar = mainwin->menuBar();
	QMenu * menu = menuBar->addMenu(QObject::tr("&File"));
	QToolBar * tool = mainwin->addToolBar(QObject::tr("&File"));
	MenuFile::inst().populate(menu, tool);

	menu = menuBar->addMenu(QObject::tr("&View"));
	MenuView::inst().populate(menu);

	menu = menuBar->addMenu(QObject::tr("&Edit"));
	tool = mainwin->addToolBar(QObject::tr("&Edit"));
	MenuEdit::inst().populate(menu, tool);

	menu = menuBar->addMenu(QObject::tr("&Action"));
	tool = mainwin->addToolBar(QObject::tr("&Action"));
	MenuAction::inst().populate(menu, tool);

	menu = menuBar->addMenu(QObject::tr("&Help"));
	tool = mainwin->addToolBar(QObject::tr("&Help"));
	MenuHelp::inst().populate(menu, tool);
}