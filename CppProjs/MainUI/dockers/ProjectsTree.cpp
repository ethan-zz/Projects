// ------ Copyright -----
// by Zhengrong Zhou 2015
// ----------------------

#include "ProjectsTree.hxx"

#include <QtGui/qicon.h>
#include <QtWidgets/qlayout.h>
#include <QtWidgets/qaction.h>
#include <QtWidgets/qtoolbar.h>
#include <QtWidgets/qtreewidget.h>
#include <QtWidgets/qtoolbutton.h>
#include <QtWidgets/qmenu.h>
#include <QtWidgets/qmessagebox.h>

ProjectsTree & ProjectsTree::inst()
{
	static ProjectsTree s_me;
	return s_me;
}

ProjectsTree::ProjectsTree(void)
	: m_tree(nullptr)
{
}

ProjectsTree::~ProjectsTree(void)
{
}

QWidget * ProjectsTree::widget()
{
	if (nullptr == m_tree)
	{
		QWidget * treeHost = new QWidget;
		treeHost->setWindowTitle(tr("Projects"));
		treeHost->setWindowIcon(QIcon(":/MenuIcons/tree.png"));
		QVBoxLayout * layout = new QVBoxLayout;
		treeHost->setLayout(layout);
		QToolBar * toolbar = new QToolBar(treeHost);
		layout->addWidget(toolbar);
		m_tree = new QTreeWidget(treeHost);
		layout->addWidget(m_tree);

		m_tree->setColumnCount(1);
		//m_tree->setHeaderLabel("Projects");
		m_tree->setHeaderHidden(true);
		//m_tree->setStyleSheet("QTreeView::branch { background: palette(base); }");
		//m_tree->setRootIsDecorated(true);

		{
			QMenu * addMenu = new QMenu(toolbar);
			QToolButton * button = new QToolButton(toolbar);
			button->setIcon(QIcon(":/MenuIcons/add.gif"));
			button->setMenu(addMenu);
			toolbar->addWidget(button);
			QAction * act = new QAction(tr("New F&older"), toolbar);
			{
				act->setIcon(QIcon(":/MenuIcons/folder.gif"));
				act->setStatusTip(tr("Create new folder"));
				connect(act, SIGNAL(triggered()), this, SLOT(createFolder()));
				addMenu->addAction(act);
			}
			act = new QAction(tr("&Import"), toolbar);
			{
				act->setIcon(QIcon(":/MenuIcons/import.gif"));
				act->setStatusTip(tr("Import a file"));
				connect(act, SIGNAL(triggered()), this, SLOT(import()));
				addMenu->addAction(act);
			}

			act = new QAction(tr("Sort Projects"), toolbar);
			{
				act->setIcon(QIcon(":/MenuIcons/sort.gif"));
				act->setStatusTip(tr("Sort projects alphabetically"));
				connect(act, SIGNAL(triggered()), this, SLOT(sortProjects()));
				toolbar->addAction(act);
			}
		}
	}
	return m_tree->parentWidget();
}

static enum class TREE_DATA_TYPE { FILEPATH = 100000, NODENAME = 100001, FOLDER = 100002 };

static void setFolderType(QTreeWidgetItem* node, bool isFolder)
{
	node->setData(0, int(TREE_DATA_TYPE::FOLDER), isFolder);
	if (isFolder)
		node->setIcon(0, QIcon(":/MenuIcons/folder.gif"));
}
static bool isNodeFolder(QTreeWidgetItem* node)
{
	bool isFolder = false;

	QVariant var = node->data(0, int(TREE_DATA_TYPE::FOLDER));
	if (var.isValid())
		isFolder = var.toBool();

	return isFolder;
}
static void setProjectPath(QTreeWidgetItem* node, const QString & path)
{
	node->setData(0, int(TREE_DATA_TYPE::FILEPATH), path);
	setFolderType(node, true);
}
static QString getProjectPath(QTreeWidgetItem* node)
{
	QString path;

	QVariant var = node->data(0, int(TREE_DATA_TYPE::FILEPATH));
	if (var.isValid())
		path = var.toString();
	return path;
}
static void setNodeName(QTreeWidgetItem* node, const QString & name)
{
	node->setData(0, int(TREE_DATA_TYPE::NODENAME), name);
	node->setText(0, name);
}
static QString getNodeName(QTreeWidgetItem* node)
{
	QString name;

	QVariant var = node->data(0, int(TREE_DATA_TYPE::NODENAME));
	if (var.isValid())
		name = var.toString();
	return name;
}

ProjectsTree::RESULT_TYPE ProjectsTree::addProject(const QString & projPath, const QString & name)
{
	RESULT_TYPE result = RESULT_TYPE::OK;

	if (nullptr == m_tree)
		widget();

	for (int i = 0; i < m_tree->topLevelItemCount(); ++i)
	{
		_ASSERT(!getProjectPath(m_tree->topLevelItem(i)).isEmpty());
		if (getProjectPath(m_tree->topLevelItem(i)) == projPath)
		{
			result = RESULT_TYPE::ERROR_ALREADY_EXIST;
			break;
		}
	}

	if (RESULT_TYPE::OK == result)
	{
		QTreeWidgetItem * node = new QTreeWidgetItem;
		setProjectPath(node, projPath);
		setNodeName(node, name);
		node->setToolTip(0, projPath);
		m_tree->addTopLevelItem(node);

		{
			QTreeWidgetItem * test = new QTreeWidgetItem;
			setNodeName(test, "Test Node 1");
			test->setIcon(0, QIcon(":/MenuIcons/file.gif"));
			node->addChild(test);

			test = new QTreeWidgetItem;
			setNodeName(test, "Test Node 2");
			test->setIcon(0, QIcon(":/MenuIcons/file.gif"));
			node->addChild(test);
		}
	}
	return result;
}

void ProjectsTree::newFolder(const QString & name)
{
	static const char * s_newFolder = "New Folder";
	static int s_count = 1;
	QString sname = name;
	if (sname.isEmpty())
		sname = QString("%1 %2").arg(s_newFolder).arg(s_count++);

	//TODO: check node existance

	//TODO: Find current node and traverse to its nearest ancestor folder
	QTreeWidgetItem * parentFolder = nullptr;
	if (nullptr == parentFolder)
	{
		if (nullptr == m_tree)
			widget();
		_ASSERT(m_tree->topLevelItemCount() > 0);
		parentFolder = m_tree->topLevelItem(0);
	}

	if (parentFolder)
	{
		QTreeWidgetItem * node = new QTreeWidgetItem;
		setNodeName(node, name);
		setFolderType(node, true);
		parentFolder->addChild(node);
	}
}

void ProjectsTree::createFolder()
{
	QMessageBox::warning(nullptr, tr("Coming soon"), "Hook up to create new folder");
}

void ProjectsTree::import()
{
	QMessageBox::warning(nullptr, tr("Coming soon"), "Hook up to import files");
}

void ProjectsTree::sortProjects()
{
	QMessageBox::warning(nullptr, tr("Coming soon"), "Hook up to sort projects");
}
