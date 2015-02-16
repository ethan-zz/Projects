// ------ Copyright -----
// by Zhengrong Zhou 2015
// ----------------------

#include "MenuFile.hxx"
#include "MainWindow.hxx"
#include "dockers/ProjectsTree.hxx"

#include <QtCore/qfile.h>
#include <QtCore/qdir.h>

#include <QtWidgets/qapplication.h>
#include <QtWidgets/qaction.h>
#include <QtWidgets/qmenu.h>
#include <QtWidgets/qtoolbar.h>
#include <QtWidgets/qfiledialog.h>
#include <QtWidgets/qinputdialog.h>
#include <QtWidgets/qtoolbutton.h>
#include <QtWidgets/qmessagebox.h>

MenuFile & MenuFile::inst()
{
	static MenuFile s_me;
	return s_me;
}

MenuFile::MenuFile(void)
{
}

MenuFile::~MenuFile(void)
{
}

void MenuFile::populate(QMenu * menu, QToolBar * toolbar)
{
	QMenu * newMenus = new QMenu(tr("Ne&w"), menu);
	menu->addMenu(newMenus);

	QToolButton * button = new QToolButton(toolbar);
	button->setIcon(QIcon(":/MenuIcons/add.gif"));
	button->setMenu(newMenus);
	toolbar->addWidget(button);
	QAction * act = new QAction(tr("&New Project"), newMenus);
	{
		act->setIcon(QIcon(":/MenuIcons/file.gif"));
		act->setShortcut(QKeySequence::New);
		act->setStatusTip(tr("Create new project"));
		connect(act, SIGNAL(triggered()), this, SLOT(newProject()));
		newMenus->addAction(act);
	}
	act = new QAction(tr("New F&older"), newMenus);
	{
		act->setIcon(QIcon(":/MenuIcons/folder.gif"));
		act->setStatusTip(tr("Create new folder"));
		connect(act, SIGNAL(triggered()), this, SLOT(newFolder()));
		newMenus->addAction(act);
	}

	act = new QAction(tr("&Open"), menu);
	{
		act->setIcon(QIcon(":/MenuIcons/open_new.gif"));
		act->setShortcut(QKeySequence::Open);
		act->setStatusTip(tr("Open a project"));
		connect(act, SIGNAL(triggered()), this, SLOT(open()));
		menu->addAction(act);
		toolbar->addAction(act);
	}

	act = new QAction(tr("&Save"), menu);
	{
		act->setIcon(QIcon(":/MenuIcons/save.gif"));
		act->setShortcut(QKeySequence::Save);
		act->setStatusTip(tr("Save a project"));
		connect(act, SIGNAL(triggered()), this, SLOT(save()));
		menu->addAction(act);
		toolbar->addAction(act);
	}

	act = new QAction(tr("Save &As"), menu);
	{
		act->setIcon(QIcon(":/MenuIcons/saveas.gif"));
		act->setShortcut(QKeySequence::SaveAs);
		act->setStatusTip(tr("Open a project"));
		connect(act, SIGNAL(triggered()), this, SLOT(saveas()));
		menu->addAction(act);
		toolbar->addAction(act);
	}

	menu->addSeparator();
	toolbar->addSeparator();

	act = new QAction(tr("&Import"), menu);
	{
		act->setIcon(QIcon(":/MenuIcons/import.gif"));
		act->setShortcut(tr("Ctrl+I"));
		act->setStatusTip(tr("Import an image file"));
		connect(act, SIGNAL(triggered()), this, SLOT(import()));
		menu->addAction(act);
		toolbar->addAction(act);
	}

	act = new QAction(tr("&Export"), menu);
	{
		act->setIcon(QIcon(":/MenuIcons/export.gif"));
		act->setShortcut(tr("Ctrl+E"));
		act->setStatusTip(tr("Export an image file"));
		connect(act, SIGNAL(triggered()), this, SLOT(exportImage()));
		menu->addAction(act);
		toolbar->addAction(act);
	}

	menu->addSeparator();
	toolbar->addSeparator();

	act = new QAction(tr("&Print"), menu);
	{
		act->setIcon(QIcon(":/MenuIcons/print.gif"));
		act->setShortcut(QKeySequence::Print);
		act->setStatusTip(tr("Print"));
		connect(act, SIGNAL(triggered()), this, SLOT(print()));
		menu->addAction(act);
		toolbar->addAction(act);
	}
}

void MenuFile::import()
{
	//TODO: put file object under folder/project
	QString filePath = QFileDialog::getOpenFileName(nullptr, tr("Import Image File"),
		"",
		tr("Images (*.png *.xpm *.jpg)"));
	if (QFile::exists(filePath))
	{
		if (!m_allFilePaths.contains(filePath))
			m_allFilePaths.prepend(filePath);
		m_currFilePath = filePath;

		if (!MainWindow::getAppMainWindow()->loadImage(filePath))
		{
			QString err = QString(tr("Failed to open image file \"%1\". \n")).arg(filePath);
			QMessageBox::warning(nullptr, tr("Fail to Import File"), err);
		}
	}
}

void MenuFile::newProject()
{
	static const char * s_untitled = "Untitled";
	static int s_count = 1;

	QString projName = QInputDialog::getText(0, tr("New project name"), tr("Enter project name:"), QLineEdit::Normal, tr("%1%2").arg(s_untitled).arg(s_count++));
	if (projName.isEmpty())
		projName = QString("%1%2").arg(s_untitled).arg(s_count++);

	QString projPath = QDir::toNativeSeparators(QDir::tempPath()) + QDir::separator() + projName + ".zproj";
	ProjectsTree::inst().addProject(projPath, projName);
}

void MenuFile::open()
{
	QMessageBox::warning(nullptr, tr("Coming soon"), "Open a project");
}

void MenuFile::save()
{
	QMessageBox::warning(nullptr, tr("Coming soon"), "Save a project");
}

void MenuFile::saveas()
{
	QMessageBox::warning(nullptr, tr("Coming soon"), "Save a project in new path");
}

void MenuFile::newFolder()
{
	QMessageBox::warning(nullptr, tr("Coming soon"), "Create folder");
}

void MenuFile::exportImage()
{
	QMessageBox::warning(nullptr, tr("Coming soon"), "Export an image file");
}

void MenuFile::print()
{
	QMessageBox::warning(nullptr, tr("Coming soon"), "Hook up to editor's print");
}