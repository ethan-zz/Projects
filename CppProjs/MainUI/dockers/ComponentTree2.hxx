// ------ Copyright -----
// by Zhengrong Zhou 2015
// ----------------------

#pragma once

#include <QtCore/qobject.h>

class QWidget;
class QTreeWidget;

class ComponentTree2 : public QObject
{
	Q_OBJECT

public:
	static ComponentTree2 & inst();
	~ComponentTree2(void);

	QWidget * widget();

private:
	QTreeWidget * m_tree;
	ComponentTree2(void);
	Q_DISABLE_COPY(ComponentTree2)
};

