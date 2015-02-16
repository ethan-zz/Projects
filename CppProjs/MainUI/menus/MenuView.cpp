// ------ Copyright -----
// by Zhengrong Zhou 2015
// ----------------------

#include "MenuView.hxx"
#include "dockers/DockerManager.hxx"
#include "dockers/ProjectsTree.hxx"
#include "dockers/ComponentTree1.hxx"
#include "dockers/ComponentTree2.hxx"
#include "dockers/Log.hxx"

#include <QtWidgets/qmenu.h>

MenuView & MenuView::inst()
{
	static MenuView s_me;
	return s_me;
}

MenuView::MenuView(void)
{
}

MenuView::~MenuView(void)
{
}

enum class DockerType { PROPERTY_DOCKER, RESULTS_DOCKER, WORKSPACE_DOCKER };

static const char * s_qwidget_property = "QWIDGET";
static const char * s_docktype_property = "DOCKERTYPE";
static QAction * createCheckableActionForWidgetInMneu(DockerType type, QWidget * widget, QMenu * menu)
{
	QAction * act = new QAction(widget->windowTitle(), menu);
	act->setStatusTip("Show " + widget->windowTitle());
	act->setCheckable(true);
	act->setChecked(true);
	act->setProperty(s_qwidget_property, VPtr<QWidget>::asQVariant(widget));
	act->setProperty(s_docktype_property, int(type));
	widget->setProperty(s_docktype_property, int(type));
	menu->addAction(act);
	return act;
}
void MenuView::populate(QMenu * menu)
{
	{
		QWidget * widget = ProjectsTree::inst().widget();
		QAction * act = createCheckableActionForWidgetInMneu(DockerType::WORKSPACE_DOCKER, widget, menu);
		connect(&DockerManager::inst(), SIGNAL(workspaceDockerVisibilityChanged(bool)), act, SLOT(setChecked(bool)));
		connect(act, SIGNAL(toggled(bool)), &DockerManager::inst(), SIGNAL(setWorkspaceDockerVisibility(bool)));
		DockerManager::inst().addProjectsTab(widget);
	}

	{
		QWidget * widget = ComponentTree1::inst().widget();
		QAction * act = createCheckableActionForWidgetInMneu(DockerType::PROPERTY_DOCKER, widget, menu);
		connect(act, SIGNAL(toggled(bool)), this, SLOT(menuItemToggled(bool)));
		connect(this, SIGNAL(checkComponent1(bool)), act, SLOT(setChecked(bool)));
		DockerManager::inst().addPropertyTab(widget);
	}

	{
		QWidget * widget = ComponentTree2::inst().widget();
		QAction * act = createCheckableActionForWidgetInMneu(DockerType::PROPERTY_DOCKER, widget, menu);
		connect(act, SIGNAL(toggled(bool)), this, SLOT(menuItemToggled(bool)));
		connect(this, SIGNAL(checkComponent2(bool)), act, SLOT(setChecked(bool)));
		DockerManager::inst().addPropertyTab(widget);
	}

	{
		QWidget * widget = Log::inst().widget();
		QAction * act = createCheckableActionForWidgetInMneu(DockerType::RESULTS_DOCKER, widget, menu);
		connect(act, SIGNAL(toggled(bool)), this, SLOT(menuItemToggled(bool)));
		connect(this, SIGNAL(checkStatusText(bool)), act, SLOT(setChecked(bool)));
		DockerManager::inst().addResultsTab(widget);
	}

	connect(&DockerManager::inst(), SIGNAL(uncheckForWidget(QWidget *)), this, SLOT(uncheckWidget(QWidget *)));
}

void MenuView::menuItemToggled(bool checked)
{
	QAction * me = qobject_cast<QAction*>(sender());
	if (me)
	{
		QVariant var = me->property(s_qwidget_property);
		QWidget * widget = VPtr<QWidget>::asPtr(var);
		var = me->property(s_docktype_property);
		DockerType type = DockerType(var.toInt());

		if (type == DockerType::PROPERTY_DOCKER)
		{
			if (checked)
				DockerManager::inst().addPropertyTab(widget);
			else
				DockerManager::inst().removePropertyTab(widget);
		}
		else if (type == DockerType::RESULTS_DOCKER)
		{
			if (checked)
				DockerManager::inst().addResultsTab(widget);
			else
				DockerManager::inst().removeResultsTab(widget);
		}
	}
}

void MenuView::uncheckWidget(QWidget * widget)
{
	if (widget)
	{
		QVariant var = widget->property(s_docktype_property);
		DockerType type = DockerType(var.toInt());

		if (type == DockerType::PROPERTY_DOCKER)
		{
			if (ComponentTree1::inst().widget() == widget)
				emit checkComponent1(false);
			else if (ComponentTree2::inst().widget() == widget)
				emit checkComponent2(false);
		}
		else if (type == DockerType::RESULTS_DOCKER)
		{
			if (Log::inst().widget() == widget)
				emit checkStatusText(false);
		}
	}
}
