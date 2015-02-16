// ------ Copyright -----
// by Zhengrong Zhou 2015
// ----------------------

#include "MenuEdit.hxx"

#include <QtWidgets/qmenu.h>
#include <QtWidgets/qtoolbar.h>
#include <QtWidgets/qmessagebox.h>

MenuEdit & MenuEdit::inst()
{
	static MenuEdit s_me;
	return s_me;
}

MenuEdit::MenuEdit(void)
{
}

MenuEdit::~MenuEdit(void)
{
}

void MenuEdit::populate(QMenu * menu, QToolBar * toolbar)
{
	QAction * act = new QAction(tr("&Copy"), menu);
	{
		act->setIcon(QIcon(":/MenuIcons/copy.gif"));
		act->setShortcut(QKeySequence::Copy);
		act->setStatusTip(tr("Copy"));
		connect(act, SIGNAL(triggered()), this, SLOT(copy()));
		menu->addAction(act);
		toolbar->addAction(act);
	}

	act = new QAction(tr("&Cut"), menu);
	{
		act->setIcon(QIcon(":/MenuIcons/cut.gif"));
		act->setShortcut(QKeySequence::Cut);
		act->setStatusTip(tr("Cut"));
		connect(act, SIGNAL(triggered()), this, SLOT(cut()));
		menu->addAction(act);
		toolbar->addAction(act);
	}

	act = new QAction(tr("&Paste"), menu);
	{
		act->setIcon(QIcon(":/MenuIcons/paste.gif"));
		act->setShortcut(QKeySequence::Paste);
		act->setStatusTip(tr("Paste"));
		connect(act, SIGNAL(triggered()), this, SLOT(paste()));
		menu->addAction(act);
		toolbar->addAction(act);
	}
}

void MenuEdit::copy()
{
	QMessageBox::warning(nullptr, tr("Coming soon"), "Hook up to specific editor's copy");
}

void MenuEdit::cut()
{
	QMessageBox::warning(nullptr, tr("Coming soon"), "Hook up to specific editor's cut");
}

void MenuEdit::paste()
{
	QMessageBox::warning(nullptr, tr("Coming soon"), "Hook up to specific editor's paste");
}