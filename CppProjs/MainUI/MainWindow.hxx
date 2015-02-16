// ------ Copyright -----
// by Zhengrong Zhou 2015
// ----------------------

#pragma once

#include <QtWidgets/qmainwindow.h>

class QMdiArea;

class MainWindow :
	public QMainWindow
{
	Q_OBJECT

public:
	static MainWindow * getAppMainWindow();

	MainWindow(void);
	~MainWindow(void);

	void initialize();
	void saveApplicationSettings();
	void restoreApplicationSettings();

	bool loadImage(const QString & filePath);
	QStringList getAllImageFilePaths() const;

private:
	void createDockers();

private:
	QMdiArea * m_mdi;
	Q_DISABLE_COPY(MainWindow);
};

