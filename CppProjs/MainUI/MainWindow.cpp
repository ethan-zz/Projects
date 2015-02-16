// ------ Copyright -----
// by Zhengrong Zhou 2015
// ----------------------

#include "MainWindow.hxx"
#include "dockers/DockerManager.hxx"
#include "menus/MainMenus.h"
#include "editors/ImageEditor.hxx"

#include <tuple>

#include <QtCore/qsettings.h>

#include <QtWidgets/qscrollarea.h>
#include <QtWidgets/qmdiarea.h>
#include <QtWidgets/qmdisubwindow.h>
#include <QtWidgets/qlabel.h>

static QSettings s_appSettings("WWW Corp.", "Explorer ME");

extern MainWindow * g_appMainWindow;

typedef std::tuple<QString, QMdiSubWindow*, ImageEditor*> SubwinTripleType;
static QList<SubwinTripleType> getAllSubWindowsDetails(QMdiArea & mdi);

MainWindow * MainWindow::getAppMainWindow()
{
	return g_appMainWindow;
}

MainWindow::~MainWindow(void)
{
}

MainWindow::MainWindow(void)
	: m_mdi(nullptr)
{
}

void MainWindow::initialize()
{
	m_mdi = new QMdiArea(this);
	setCentralWidget(m_mdi);
	createDockers();
	MainMenus::inst().populateMenubar(this);
}

void MainWindow::createDockers()
{
	DockerManager::inst().createDockers(this);
}

bool MainWindow::loadImage(const QString & filePath)
{
	QMdiSubWindow * subwin = nullptr;
	ImageEditor * editor = nullptr;
	QList<SubwinTripleType> subwins = getAllSubWindowsDetails(*m_mdi);
	foreach(auto & one, subwins)
	{
		QString path;
		QMdiSubWindow * tmpwin = nullptr;
		ImageEditor * tmpeditor = nullptr;
		std::tie(path, tmpwin, tmpeditor) = one;
		if (path == filePath)
		{
			subwin = tmpwin;
			editor = tmpeditor;
			break;
		}
	}

	bool bOK = true;
	if (!editor)
	{
		editor = new ImageEditor(this);
		bOK = editor->loadImageFile(filePath);
		if (bOK)
		{
			QScrollArea * scrollArea = new QScrollArea(this);
			scrollArea->setWidget(editor);
			subwin = m_mdi->addSubWindow(scrollArea);
		}
	}
	else
		bOK = editor->loadImageFile(filePath);

	if (bOK)
		subwin->show();

	return bOK;
}

QStringList MainWindow::getAllImageFilePaths() const
{
	QStringList paths;
	QList<SubwinTripleType> subwins = getAllSubWindowsDetails(*m_mdi);
	foreach(auto & one, subwins)
	{
		QString path;
		QMdiSubWindow * subwin;
		ImageEditor * editor;
		std::tie(path, subwin, editor) = one;
		paths.append(path);
	}
	return paths;
}

static QList<SubwinTripleType> getAllSubWindowsDetails(QMdiArea & mdi)
{
	QList<SubwinTripleType>  details;

	QMdiSubWindow * subwin = nullptr;
	ImageEditor * editor = nullptr;
	QList<QMdiSubWindow *> subs = mdi.subWindowList(QMdiArea::ActivationHistoryOrder);
	foreach(auto sub, subs)
	{
		QScrollArea * scroll = qobject_cast<QScrollArea *>(sub->widget());
		if (scroll)
		{
			ImageEditor * me = qobject_cast<ImageEditor *>(scroll->widget());
			if (me)
				details.append(std::make_tuple(me->imageFilePath(), sub, me));
		}
	}

	return details;
}

static const char * s_str_main_geom = "Main Geometry";
void MainWindow::saveApplicationSettings()
{
	s_appSettings.setValue(s_str_main_geom, saveGeometry());
}

void MainWindow::restoreApplicationSettings()
{
	restoreGeometry(s_appSettings.value(s_str_main_geom).toByteArray());
}
