// ------ Copyright -----
// by Zhengrong Zhou 2015
// ----------------------

#pragma once

#include <QtCore/qobject.h>

class QWidget;
class QTreeWidget;

class ComponentTree1 : public QObject
{
	Q_OBJECT

public:
	static ComponentTree1 & inst();
	~ComponentTree1(void);

	QWidget * widget();

private:
	QTreeWidget * m_tree;
	ComponentTree1(void);
	Q_DISABLE_COPY(ComponentTree1)
};

