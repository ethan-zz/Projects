// ------ Copyright -----
// by Zhengrong Zhou 2015
// ----------------------

#include "StringUtility.h"

#include <QtCore/qstring.h>

namespace StringUtility
{
	std::string QString2StdString(const QString & qstr)
	{
		return std::string(qstr.toUtf8().data());
	}
}