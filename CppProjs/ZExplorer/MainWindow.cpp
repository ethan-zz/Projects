// ------ Copyright -----
// by Zhengrong Zhou 2014
// ----------------------
#include "MainWindow.h"
#include "FileMenuManager.h"
#include "WorkspaceTree.h"
#include <QtGui/QCloseEVent>
#include <QtCore/QSettings>
#include <QtWidgets/QApplication>
#include <QtWidgets/QDesktopWidget>
#include <QtWidgets/QMenuBar>
#include <QtWidgets/QMdiArea>
#include <QtWidgets/QDockWidget>

namespace zz
{
	MainWindow & MainWindow::instance()
	{
		static MainWindow s_me;
		return s_me;
	}
	MainWindow::MainWindow()
	{
		if (!restoreSettings())
		{
			const QRect screen = QApplication::desktop()->availableGeometry();
			setGeometry(screen.width() / 4, screen.height() / 4, screen.width() / 2, screen.height() / 2);
		}
		setWindowIcon(QIcon(":/Icons/ZExplore.png"));

		populateMenubar();

		m_mdiArea = new QMdiArea;
		setCentralWidget(m_mdiArea);

		m_wspTree = new QDockWidget(tr("Workspace Tree"));
		m_wspTree->setFeatures(QDockWidget::NoDockWidgetFeatures);
		m_wspTree->setObjectName("WorkspaceTreeDockWidget");
		m_wspTree->setWidget(WorkspaceTree::instance());
		m_wspTree->setAllowedAreas(Qt::LeftDockWidgetArea | Qt::RightDockWidgetArea);
		addDockWidget(Qt::LeftDockWidgetArea, m_wspTree);
	}
	MainWindow::~MainWindow()
	{

	}

	void MainWindow::closeEvent(QCloseEvent * evt)
	{
		evt->accept();
		storeSettings();
	}

	const QString s_companyName("ZZ Corp.");
	const QString s_appName("ZExplore");
	const QString s_geometry("geometry");
	void MainWindow::storeSettings()
	{
		QSettings settings(tr(s_companyName.toStdString().c_str()), tr(s_appName.toStdString().c_str()));
		settings.setValue(s_geometry, saveGeometry());
	}

	bool MainWindow::restoreSettings()
	{
		QSettings settings(tr(s_companyName.toStdString().c_str()), tr(s_appName.toStdString().c_str()));
		QVariant geom = settings.value(s_geometry);
		if (geom.isValid())
		{
			restoreGeometry(geom.toByteArray());
			return true;
		}
		return false;
	}

	void MainWindow::populateMenubar()
	{
		FileMenuManager::instance().populateMenu(menuBar()->addMenu(tr("&File")));
		populateViewMenu();
	}

	void MainWindow::populateViewMenu()
	{
		QMenu * view = menuBar()->addMenu(tr("&View"));
		QAction * wspTreeAction = new QAction(tr("Show &Workspace Tree"), view);
		wspTreeAction->setCheckable(true);
		wspTreeAction->setChecked(true);
		//QObject::connect(wspTreeAction, SIGNAL(toggled(bool)), m_wspTree, SLOT(setVisible(bool)));
		connect(wspTreeAction, SIGNAL(toggled(bool)), this, SLOT(showWorkspaceTree(bool)));
		view->addAction(wspTreeAction);
	}

	void MainWindow::showWorkspaceTree(bool showIt)
	{
		m_wspTree->setVisible(showIt);
	}
}

