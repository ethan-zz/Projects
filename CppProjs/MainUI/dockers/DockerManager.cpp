// ------ Copyright -----
// by Zhengrong Zhou 2015
// ----------------------

#include "DockerManager.hxx"

#include <QtWidgets/qdockwidget.h>
#include <QtWidgets/qmainwindow.h>
#include <QtWidgets/qtabwidget.h>

DockerManager::DockerManager(void)
	: m_dockProjects(nullptr)
	, m_dockProperty(nullptr)
	, m_dockResults(nullptr)
	, m_tabProjects(nullptr)
	, m_tabProperty(nullptr)
	, m_tabResults(nullptr)
{
}

DockerManager::~DockerManager(void)
{
}

DockerManager & DockerManager::inst()
{
	static DockerManager * s_me = new DockerManager();
	return *s_me;
}


void DockerManager::createDockers(QMainWindow * mainwin)
{
	bool alreadyDone = (m_dockProjects || m_dockProperty || m_dockResults);

	if (alreadyDone)
		return;
	{
		mainwin->setCorner(Qt::BottomLeftCorner, Qt::LeftDockWidgetArea);
		mainwin->setCorner(Qt::BottomRightCorner, Qt::RightDockWidgetArea);
	}

	{
		m_dockProjects = new QDockWidget(mainwin);
		m_dockProjects->setObjectName("ProjectsTreeDock");
		m_dockProjects->setAllowedAreas(Qt::LeftDockWidgetArea | Qt::RightDockWidgetArea);
		m_tabProjects = new QTabWidget(mainwin);
		m_tabProjects->setTabShape(QTabWidget::Triangular);
		m_dockProjects->setWidget(m_tabProjects);
		m_tabProjects->setTabPosition(QTabWidget::South);
		m_tabProjects->setTabsClosable(false);
		connect(m_dockProjects, SIGNAL(visibilityChanged(bool)), this, SIGNAL(workspaceDockerVisibilityChanged(bool)));
		connect(this, SIGNAL(setProjectsDockerVisibility(bool)), m_dockProjects, SLOT(setVisible(bool)));
	}

	{
		m_dockProperty = new QDockWidget(mainwin);
		m_dockProperty->setObjectName("ObjectPropertyDock");
		m_dockProperty->setAllowedAreas(Qt::LeftDockWidgetArea | Qt::RightDockWidgetArea);
		m_tabProperty = new QTabWidget(mainwin);
		m_tabProperty->setMovable(true);
		m_tabProperty->setTabShape(QTabWidget::Triangular);
		m_dockProperty->setWidget(m_tabProperty);
		m_tabProperty->setTabPosition(QTabWidget::South);
		m_tabProperty->setTabsClosable(true);
		connect(m_tabProperty, SIGNAL(tabCloseRequested(int)), this, SLOT(requestClosePropertyDockerTab(int)));
		connect(m_dockProperty, SIGNAL(visibilityChanged(bool)), this, SLOT(changePropertyDockerVisibility(bool)));
	}

	{
		m_dockResults = new QDockWidget(mainwin);
		m_dockResults->setObjectName("OperationResultsDock");
		m_dockResults->setAllowedAreas(Qt::BottomDockWidgetArea | Qt::RightDockWidgetArea);
		m_tabResults = new QTabWidget(mainwin);
		m_tabResults->setTabShape(QTabWidget::Triangular);
		m_dockResults->setWidget(m_tabResults);
		m_tabResults->setTabPosition(QTabWidget::South);
		m_tabResults->setTabsClosable(true);
		connect(m_tabResults, SIGNAL(tabCloseRequested(int)), this, SLOT(requestCloseResultsDockerTab(int)));
		connect(m_dockResults, SIGNAL(visibilityChanged(bool)), this, SLOT(changeResultsDockerVisibility(bool)));
	}

	mainwin->addDockWidget(Qt::LeftDockWidgetArea, m_dockProjects);
	mainwin->addDockWidget(Qt::RightDockWidgetArea, m_dockProperty);
	mainwin->addDockWidget(Qt::BottomDockWidgetArea, m_dockResults);
}

void DockerManager::changePropertyDockerVisibility(bool visible)
{
	if (!visible)
	{
		for (int i = 0; i < m_tabProperty->count(); ++i)
			emit uncheckForWidget(m_tabProperty->widget(i));
	}
}

void DockerManager::changeResultsDockerVisibility(bool visible)
{
	if (!visible)
	{
		for (int i = 0; i < m_tabResults->count(); ++i)
			emit uncheckForWidget(m_tabResults->widget(i));
	}
}

void DockerManager::requestClosePropertyDockerTab(int index)
{
	QWidget * widget = m_tabProperty->widget(index);

	emit uncheckForWidget(widget);
}

void DockerManager::requestCloseResultsDockerTab(int index)
{
	QWidget * widget = m_tabResults->widget(index);

	emit uncheckForWidget(widget);
}

static void addWidgetToTab(QWidget * widget, QTabWidget * tabs, QDockWidget * docker)
{
	tabs->addTab(widget, widget->windowIcon(), widget->windowTitle());
	if (tabs->count() == 1)
		docker->setVisible(true);
}
void DockerManager::addProjectsTab(QWidget * widget)
{
	addWidgetToTab(widget, m_tabProjects, m_dockProjects);
}

void DockerManager::addPropertyTab(QWidget * widget)
{
	addWidgetToTab(widget, m_tabProperty, m_dockProperty);
}

void DockerManager::addResultsTab(QWidget * widget)
{
	addWidgetToTab(widget, m_tabResults, m_dockResults);
}

static void removeWidgetFromTab(QWidget * widget, QTabWidget * tabs, QDockWidget * docker)
{
	for (int i = 0; i < tabs->count(); ++i)
	{
		if (tabs->widget(i) == widget)
		{
			tabs->removeTab(i);
			break;
		}
	}
	if (tabs->count() == 0)
		docker->setVisible(false);
}
void DockerManager::removePropertyTab(QWidget * widget)
{
	removeWidgetFromTab(widget, m_tabProperty, m_dockProperty);
}

void DockerManager::removeResultsTab(QWidget * widget)
{
	removeWidgetFromTab(widget, m_tabResults, m_dockResults);
}
