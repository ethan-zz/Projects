// ------ Copyright -----
// by Zhengrong Zhou 2014
// ----------------------
#include "vtkMainWindow.h"
#include "QtVtk/QVTKWidget.h"

#include <QtGui/QCloseEVent>
#include <QtWidgets/QApplication>
#include <QtWidgets/QDesktopWidget>
//#include <QtWidgets/QHBoxLayout>

#include <vtkSmartPointer.h>
#include <vtkSphereSource.h>
#include <vtkPolyDataMapper.h>
#include <vtkActor.h>
#include <vtkImageViewer.h>
#include <vtkRenderWindowInteractor.h>
#include <vtkInteractorStyleImage.h>
#include <vtkRenderer.h>
#include <vtkJPEGReader.h>
#include <vtkCubeSource.h>

namespace zz
{
	vtkMainWindow & vtkMainWindow::instance()
	{
		static vtkMainWindow s_me;
		return s_me;
	}
	vtkMainWindow::vtkMainWindow()
	{
		const QRect screen = QApplication::desktop()->availableGeometry();
		setGeometry(screen.width() / 4, screen.height() / 4, screen.width() / 2, screen.height() / 2);

		vtkSmartPointer<vtkSphereSource> sphereSource = vtkSmartPointer<vtkSphereSource>::New();
		sphereSource->Update();
		vtkSmartPointer<vtkPolyDataMapper> sphereMapper = vtkSmartPointer<vtkPolyDataMapper>::New();
		sphereMapper->SetInputConnection(sphereSource->GetOutputPort());
		vtkSmartPointer<vtkActor> sphereActor = vtkSmartPointer<vtkActor>::New();
		sphereActor->SetMapper(sphereMapper);

		vtkSmartPointer<vtkRenderer> renderer = vtkSmartPointer<vtkRenderer>::New();
		renderer->AddActor(sphereActor);

		//vtkSmartPointer<vtkCubeSource> cubeSource = vtkSmartPointer<vtkCubeSource>::New();
		//cubeSource->Update();
		//vtkSmartPointer<vtkPolyDataMapper> cubeMapper = vtkSmartPointer<vtkPolyDataMapper>::New();
		//cubeMapper->SetInputConnection(cubeSource->GetOutputPort());
		//vtkSmartPointer<vtkActor> cubeActor = vtkSmartPointer<vtkActor>::New();
		//cubeActor->SetMapper(cubeMapper);
		//vtkSmartPointer<vtkRenderer> rendererc = vtkSmartPointer<vtkRenderer>::New();
		//rendererc->AddActor(cubeActor);

		//vtkSmartPointer<vtkRenderWindow> renderWindow = vtkSmartPointer<vtkRenderWindow>::New();
		//renderWindow->AddRenderer(renderer);
		//renderer->ResetCamera();
		//vtk->SetRenderWindow(renderWindow);
		//vtkSmartPointer<vtkRenderWindow> renderWindowc = vtkSmartPointer<vtkRenderWindow>::New();
		//renderWindowc->AddRenderer(rendererc);
		//rendererc->ResetCamera();
		//vtkc->SetRenderWindow(renderWindowc);
		//QVTKWidget * vtkc = new QVTKWidget;
		//vtkc->GetRenderWindow()->AddRenderer(rendererc);

		QVTKWidget * vtk = new QVTKWidget;
		vtk->GetRenderWindow()->AddRenderer(renderer);

		//QWidget * holder = new QWidget;
		//QHBoxLayout * layout = new QHBoxLayout;
		//layout->addWidget(vtk);
		//layout->addWidget(vtkc);
		//holder->setLayout(layout);
		//setCentralWidget(holder);

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

