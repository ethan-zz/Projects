// ------ Copyright -----
// by Zhengrong Zhou 2015
// ----------------------

#pragma once

#include <QtCore/qobject.h>
#include <QtCore/qstringlist.h>

class QMenu;
class QToolBar;

class MenuFile : public QObject
{
	Q_OBJECT
public:
	static MenuFile & inst();
	~MenuFile(void);

	void populate(QMenu * menu, QToolBar * toolbar);
	const QString & getCurrFilePath() const { return m_currFilePath; }

	private slots:
	void newProject();
	void open();
	void save();
	void saveas();
	void newFolder();
	void import();
	void exportImage();
	void print();

private:
	QString m_currFilePath;
	QStringList m_allFilePaths;

private:
	MenuFile(void);
	Q_DISABLE_COPY(MenuFile)
};

