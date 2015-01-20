// ------ Copyright -----
// by Zhengrong Zhou 2014
// ----------------------

#pragma once

#include <QtWidgets/QMainWIndow>

namespace zz
{
	class vtkMainWindow : public QMainWindow
	{
		Q_OBJECT
	public:
		static vtkMainWindow & instance();
		~vtkMainWindow();

	protected:
		void closeEvent(QCloseEvent * evt) override;

	private:
		vtkMainWindow();
		Q_DISABLE_COPY(vtkMainWindow)
	};
}


