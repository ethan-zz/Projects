// ------ Copyright -----
// by Zhengrong Zhou 2015
// ----------------------

#pragma once
#include <QtWidgets/qwidget.h>
#include <QtGui/qimage.h>

class QPixmap;

class ImageEditor :
	public QWidget
{
	Q_OBJECT

public:
	ImageEditor(QWidget * parent);
	~ImageEditor(void);

	bool loadImageFile(const QString & filePath);
	QSize sizeHint() const override;
	const QString & imageFilePath() const { return m_imagePath; }

protected:
	void paintEvent(QPaintEvent * evt) override;

private:
	QString m_imagePath;
	QImage m_image;
};

