// ------ Copyright -----
// by Zhengrong Zhou 2014
// ----------------------
#include "vtkMainWindow.h"
#include "QVTKWidget/QVTKWidget.h"

#include <QtGui/QCloseEVent>
#include <QtWidgets/QApplication>
#include <QtWidgets/QDesktopWidget>

#include <vtkSmartPointer.h>
#include <vtkContextView.h>
#include <vtkContextScene.h>
#include <vtkChartXYZ.h>
#include <vtkTable.h>
#include <vtkDoubleArray.h>
#include <vtkPlotSurface.h>
#include <vtkRenderWindow.h>

namespace zz
{
	static float sinc(double x)
	{
		if (x == 0)
			return 1;
		return sin(x) / x;
	}
	vtkMainWindow & vtkMainWindow::instance()
	{
		static vtkMainWindow s_me;
		return s_me;
	}
	vtkMainWindow::vtkMainWindow()
	{
		const QRect screen = QApplication::desktop()->availableGeometry();
		setGeometry(screen.width() / 4, screen.height() / 4, screen.width() / 2, screen.height() / 2);

		vtkSmartPointer<vtkTable> table = vtkSmartPointer<vtkTable>::New();
		double numPoints = 150;
		double diameter = 15;
		double radius = diameter / 2;
		double inc = diameter / (numPoints - 1);
		for (double i = 0; i < numPoints; ++i)
		{
			vtkSmartPointer<vtkDoubleArray> arr = vtkSmartPointer<vtkDoubleArray>::New();
			table->AddColumn(arr.GetPointer());
		}
		table->SetNumberOfRows(numPoints);
		for (double i = 0; i < numPoints; ++i)
		{
			double x = i * inc - radius;
			for (double j = 0; j < numPoints; ++j)
			{
				double y = j * inc - radius;
				table->SetValue(i, j, sinc(sqrt(x*x + y*y)));
			}
		}

		vtkSmartPointer<vtkContextView> view = vtkSmartPointer<vtkContextView>::New();
		vtkSmartPointer<vtkChartXYZ>    chart = vtkSmartPointer<vtkChartXYZ>::New();
		QRect sz = geometry();
		chart->SetGeometry(vtkRectf(0, 0, sz.width(), sz.height()));
		view->GetScene()->AddItem(chart.GetPointer());

		QVTKWidget * vtk = new QVTKWidget;
		view->SetRenderWindow(vtk->GetRenderWindow());

		vtkSmartPointer<vtkPlotSurface> plot = vtkSmartPointer<vtkPlotSurface>::New();
		plot->SetXRange(0, numPoints);
		plot->SetYRange(0, numPoints);
		plot->SetInputData(table.GetPointer());
		chart->AddPlot(plot.GetPointer());

		view->GetRenderWindow()->SetMultiSamples(0);
		view->GetRenderWindow()->Render();

		setCentralWidget(vtk);
	}
	vtkMainWindow::~vtkMainWindow()
	{

	}

	void vtkMainWindow::closeEvent(QCloseEvent * evt)
	{
		evt->accept();
	}

}

