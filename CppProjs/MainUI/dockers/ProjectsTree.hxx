// ------ Copyright -----
// by Zhengrong Zhou 2015
// ----------------------

#pragma once

#include <QtCore/qobject.h>

class QWidget;
class QTreeWidget;

class ProjectsTree : public QObject
{
	Q_OBJECT

public:
	enum class RESULT_TYPE { OK = 0, ERROR_ALREADY_EXIST };
	static ProjectsTree & inst();
	~ProjectsTree(void);

	QWidget * widget();

	RESULT_TYPE addProject(const QString & projPath, const QString & name);

	public slots:
	void newFolder(const QString & name);
	void createFolder();
	void import();
	void sortProjects();

private:
	QTreeWidget * m_tree;
	ProjectsTree(void);
	Q_DISABLE_COPY(ProjectsTree)
};

