// ------ Copyright -----
// by Zhengrong Zhou
// ----------------------
#include "FileMenuManager.h"
#include <QtWidgets/QMenu>
#include <QtWidgets/QAction>
#include <QtWidgets/QApplication>
#include <QtWidgets/QMessageBox>

namespace zz
{
	FileMenuManager& FileMenuManager::instance()
	{
		static FileMenuManager s_me;
		return s_me;
	}
	FileMenuManager::FileMenuManager(QObject * parent)
		: QObject(parent)
		, m_pNewAction(nullptr)
	{
	}

	FileMenuManager::~FileMenuManager()
	{
	}

	void FileMenuManager::populateMenu(QMenu * pMenu)
	{
		createActions(pMenu);
	}

	void FileMenuManager::createActions(QMenu * pMenu)
	{
		m_pNewAction = new QAction(tr("&New"), pMenu);
		m_pNewAction->setIcon(QIcon(":/Icons/new.png"));
		m_pNewAction->setShortcut(QKeySequence::New);
		m_pNewAction->setToolTip(tr("Create new project."));
		m_pNewAction->setStatusTip(tr("Create new project."));
		connect(m_pNewAction, SIGNAL(triggered()), this, SLOT(newProject()));
		pMenu->addAction(m_pNewAction);

		QAction * exitAction = new QAction(tr("E&xit"), pMenu);
		exitAction->setShortcut(tr("Ctrl+Q"));
		exitAction->setStatusTip(tr("Exit application."));
		connect(exitAction, SIGNAL(triggered()), (QApplication*)(QApplication::instance()), SLOT(closeAllWindows()));
		pMenu->addAction(exitAction);

	}

	void FileMenuManager::newProject()
	{
		QMessageBox::information(nullptr, "To Do", "Yet to come");
	}
}

