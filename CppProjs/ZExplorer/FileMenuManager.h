// ------ Copyright -----
// by Zhengrong Zhou 2014
// ----------------------
#pragma once
#include <QtCore/QObject>

class QMenu;
class QAction;
namespace zz
{
	class FileMenuManager : public QObject
	{
		Q_OBJECT
	public:
		~FileMenuManager();
		static FileMenuManager & instance();

		void populateMenu(QMenu * pMenu);
	private:
		void createActions(QMenu * pMenu);

		private slots:
		void newProject();

	private:
		QAction * m_pNewAction;

		FileMenuManager(QObject * parent = nullptr);
		Q_DISABLE_COPY(FileMenuManager);
	};
}


