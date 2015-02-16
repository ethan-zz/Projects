// ------ Copyright -----
// by Zhengrong Zhou 2015
// ----------------------

#pragma once

#include <QtCore/qobject.h>

class QMenu;
class QToolBar;

class MenuEdit : public QObject
{
	Q_OBJECT

public:
	static MenuEdit & inst();
	~MenuEdit(void);

	void populate(QMenu * menu, QToolBar * toolbar);

	private slots:
	void copy();
	void cut();
	void paste();

private:
	MenuEdit(void);
	Q_DISABLE_COPY(MenuEdit)
};

