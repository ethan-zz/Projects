// ------ Copyright -----
// by Zhengrong Zhou 2015
// ----------------------

#include "MenuAction.hxx"

#include <QtWidgets/qmenu.h>
#include <QtWidgets/qtoolbar.h>
#include <QtWidgets/qmessagebox.h>

MenuAction & MenuAction::inst()
{
	static MenuAction s_me;
	return s_me;
}

MenuAction::MenuAction(void)
	: m_runStopAct(nullptr)
	, m_bToRun(true)
{
}

MenuAction::~MenuAction(void)
{
}

static const QIcon * s_runIcon = nullptr;
static const QIcon * s_stopIcon = nullptr;
void MenuAction::populate(QMenu * menu, QToolBar * toolbar)
{
	if (s_runIcon == nullptr)
	{
		s_runIcon = new QIcon(":/MenuIcons/run.gif");
		s_stopIcon = new QIcon(":/MenuIcons/stop.gif");
	}

	m_runStopAct = new QAction(tr("&Run"), menu);
	{
		m_runStopAct->setIcon(*s_runIcon);
		m_runStopAct->setShortcut(tr("F5"));
		m_runStopAct->setStatusTip(tr("run/execute"));
		m_runStopAct->setToolTip(tr("run/execute"));
		connect(m_runStopAct, SIGNAL(triggered()), this, SLOT(runStopToggle()));
		menu->addAction(m_runStopAct);
		toolbar->addAction(m_runStopAct);
		m_bToRun = true;
	}
}

void MenuAction::runStopToggle()
{
	if (m_bToRun)
	{
		m_runStopAct->setIcon(*s_stopIcon);
		m_runStopAct->setShortcut(tr("Shift+F5"));
		m_runStopAct->setText(tr("Stop"));
		m_runStopAct->setStatusTip(tr("Stop"));
		m_runStopAct->setToolTip(tr("Stop"));
		QMessageBox::warning(nullptr, tr("Coming soon"), "Hook up to stop worker thread");
	}
	else
	{
		m_runStopAct->setIcon(*s_runIcon);
		m_runStopAct->setText(tr("&Run"));
		m_runStopAct->setShortcut(tr("F5"));
		m_runStopAct->setStatusTip(tr("run/execute"));
		m_runStopAct->setToolTip(tr("run/execute"));
		QMessageBox::warning(nullptr, tr("Coming soon"), "Hook up to run in worker thread");
	}
	m_bToRun = !m_bToRun;
}