// ------ Copyright -----
// by Zhengrong Zhou 2015
// ----------------------

#pragma once

#include <QtCore/qobject.h>

class QMenu;
class QToolBar;
class QAction;

class MenuAction : public QObject
{
	Q_OBJECT

public:
	static MenuAction & inst();
	~MenuAction(void);

	void populate(QMenu * menu, QToolBar * toolbar);

	private slots:
	void runStopToggle();

private:
	QAction * m_runStopAct;
	bool m_bToRun;
	MenuAction(void);
	Q_DISABLE_COPY(MenuAction)
};

