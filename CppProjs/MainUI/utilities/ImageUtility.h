// ------ Copyright -----
// by Zhengrong Zhou 2015
// ----------------------

#pragma once

#include <QtGui/qimage.h>

namespace cv
{
	class Mat;
}

namespace ImageUtility
{
	QImage  cvMatToQImage(const cv::Mat &inMat);
}