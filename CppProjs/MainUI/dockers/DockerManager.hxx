// ------ Copyright -----
// by Zhengrong Zhou 2015
// ----------------------

#pragma once
#include <QtCore/qobject.h>

class QMainWindow;
class QDockWidget;
class QTabWidget;

class DockerManager : public QObject
{
	Q_OBJECT

public:
	static DockerManager & inst();
	~DockerManager(void);

	void createDockers(QMainWindow * mainwin);

	void addProjectsTab(QWidget * widget);
	void addPropertyTab(QWidget * widget);
	void removePropertyTab(QWidget * widget);
	void addResultsTab(QWidget * widget);
	void removeResultsTab(QWidget * widget);

	private slots:
	void requestClosePropertyDockerTab(int index);
	void requestCloseResultsDockerTab(int index);
	void changePropertyDockerVisibility(bool visible);
	void changeResultsDockerVisibility(bool visible);

signals:
	void setProjectsDockerVisibility(bool visible);
	void workspaceDockerVisibilityChanged(bool visible);
	void uncheckForWidget(QWidget * widget);

private:
	QDockWidget * m_dockProjects;
	QDockWidget * m_dockProperty;
	QDockWidget * m_dockResults;
	QTabWidget * m_tabProjects;
	QTabWidget * m_tabProperty;
	QTabWidget * m_tabResults;

	DockerManager(void);
	Q_DISABLE_COPY(DockerManager)
};

