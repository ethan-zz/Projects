// ------ Copyright -----
// by Zhengrong Zhou 2015
// ----------------------

#pragma once

#include <QtCore/qobject.h>

class QWidget;
class QTextBrowser;

class Log : public QObject
{
	Q_OBJECT

public:
	static Log & inst();
	~Log(void);

	QWidget * widget();

private:
	QTextBrowser * m_text;
	Log(void);
	Q_DISABLE_COPY(Log)
};

