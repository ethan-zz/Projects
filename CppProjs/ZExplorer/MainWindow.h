// ------ Copyright -----
// by Zhengrong Zhou 2014
// ----------------------

#pragma once

#include <QtWidgets/QMainWIndow>

class QMdiArea;
class QDockWidget;
namespace zz
{
	class MainWindow : public QMainWindow
	{
		Q_OBJECT
	public:
		static MainWindow & instance();
		~MainWindow();

	protected:
		void closeEvent(QCloseEvent * evt) override;

	private:
		void storeSettings();
		bool restoreSettings();
		void populateMenubar();
		void populateViewMenu();

		private slots:
		void showWorkspaceTree(bool showIt);

	private:
		QMdiArea * m_mdiArea;
		QDockWidget * m_wspTree;
	private:
		MainWindow();
		Q_DISABLE_COPY(MainWindow)
	};
}


