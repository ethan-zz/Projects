// ------ Copyright -----
// by Zhengrong Zhou 2015
// ----------------------

#include "ImageEditor.hxx"
#include "utilities/StringUtility.h"
#include "utilities/ImageUtility.h"

#include <QtCore/qfile.h>
#include <QtGui/qpainter.h>
#include <QtWidgets/qmessagebox.h>

#include <opencv2/opencv.hpp>


ImageEditor::ImageEditor(QWidget * parent)
	: QWidget(parent)
{
}


ImageEditor::~ImageEditor(void)
{
}

bool ImageEditor::loadImageFile(const QString & filePath)
{
	if (!QFile::exists(filePath))
	{
		QString err = QString(tr("File \"%1\" does not exist. \n")).arg(filePath);
		QMessageBox::warning(this, tr("File Not Exist"), err);
		return false;
	}

	bool bOK = true;
	std::string sfile = StringUtility::QString2StdString(filePath);
	cv::Mat src = cv::imread(sfile);
	if (!src.data)
		bOK = false;
	else
	{
		m_image = ImageUtility::cvMatToQImage(src).convertToFormat(QImage::Format_ARGB32);
		update();
		updateGeometry();
	}

	//double alpha = 0.5; double beta;
	//cv::Mat dst;
	//beta = ( 1.0 - alpha );
	//addWeighted( src1, alpha, src2, beta, 0.0, dst);
	return bOK;
}

void ImageEditor::paintEvent(QPaintEvent * evt)
{
	QPainter painter(this);
	painter.drawImage(0., 0., m_image);
}

QSize ImageEditor::sizeHint() const
{
	return QSize(m_image.width(), m_image.height());
}