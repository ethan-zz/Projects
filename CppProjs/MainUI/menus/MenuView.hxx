// ------ Copyright -----
// by Zhengrong Zhou 2015
// ----------------------

#pragma once

#include <QtCore/qobject.h>
#include <QtCore/qvariant.h>

class QMenu;

template <class T> class VPtr
{
public:
	static T* asPtr(QVariant v)
	{
		return  (T *)v.value<void *>();
	}

	static QVariant asQVariant(T* ptr)
	{
		return qVariantFromValue((void *)ptr);
	}
};

class MenuView : public QObject
{
	Q_OBJECT
public:
	static MenuView & inst();
	~MenuView(void);

	void populate(QMenu * menu);

	private slots:
	void menuItemToggled(bool checked);
	void uncheckWidget(QWidget * widget);

signals:
	void checkComponent1(bool);
	void checkComponent2(bool);
	void checkStatusText(bool);

private:
	MenuView(void);
	Q_DISABLE_COPY(MenuView)
};

