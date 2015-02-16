// ------ Copyright -----
// by Zhengrong Zhou 2015
// ----------------------

#include "ComponentTree2.hxx"

#include <QtGui/qicon.h>
#include <QtWidgets/qtreewidget.h>

ComponentTree2 & ComponentTree2::inst()
{
	static ComponentTree2 s_me;
	return s_me;
}

ComponentTree2::ComponentTree2(void)
	: m_tree(nullptr)
{
}

ComponentTree2::~ComponentTree2(void)
{
}

QWidget * ComponentTree2::widget()
{
	if (nullptr == m_tree)
	{
		m_tree = new QTreeWidget;
		m_tree->setColumnCount(1);
		m_tree->setWindowTitle("Comp#2");
		m_tree->setToolTip("Components Set 2 Tree");
		m_tree->setWindowIcon(QIcon(":/MenuIcons/build.png"));
	}
	return m_tree;
}