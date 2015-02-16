// ------ Copyright -----
// by Zhengrong Zhou 2015
// ----------------------

#include "Log.hxx"

#include <QtGui/qicon.h>
#include <QtWidgets/qtextbrowser.h>

Log & Log::inst()
{
	static Log s_me;
	return s_me;
}

Log::Log(void)
	: m_text(nullptr)
{
}

Log::~Log(void)
{
}

QWidget * Log::widget()
{
	if (nullptr == m_text)
	{
		m_text = new QTextBrowser;
		m_text->setWindowTitle("Log Report");
		m_text->setWindowIcon(QIcon(":/MenuIcons/stash.jpg"));
		m_text->setReadOnly(true);
		m_text->setOpenExternalLinks(true);
		m_text->setText("<font color = \"black\">Link to</font> <a href = \"http://www.bing.com\">Bing</a>");
	}
	return m_text;
}