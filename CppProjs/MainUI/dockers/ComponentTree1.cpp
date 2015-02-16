// ------ Copyright -----
// by Zhengrong Zhou 2015
// ----------------------

#include "ComponentTree1.hxx"

#include <QtGui/qicon.h>
#include <QtWidgets/qtreewidget.h>

ComponentTree1 & ComponentTree1::inst()
{
	static ComponentTree1 s_me;
	return s_me;
}

ComponentTree1::ComponentTree1(void)
	: m_tree(nullptr)
{
}

ComponentTree1::~ComponentTree1(void)
{
}

QWidget * ComponentTree1::widget()
{
	if (nullptr == m_tree)
	{
		m_tree = new QTreeWidget;
		m_tree->setColumnCount(1);
		m_tree->setWindowTitle("Comp#1");
		m_tree->setToolTip("Components Set 1 Tree");
		m_tree->setWindowIcon(QIcon(":/MenuIcons/edit_src.png"));
	}
	return m_tree;
}
