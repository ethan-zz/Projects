// ------ Copyright -----
// by Zhengrong Zhou 2014
// ----------------------
#include "Variant.h"

namespace zz
{
	Variant::~Variant()
	{}

	Variant::Variant(int val)
		: m_type(VType::vtInt)
	{
		m_data.ival = val;
	}
	Variant::Variant(double val)
		: m_type(VType::vtDouble)
	{
		m_data.dval = val;
	}


	std::ostream & Variant::operator << (std::ostream& os) const
	{
		switch (m_type)
		{
		case VType::vtInt:
			os << m_data.ival;
			break;
		case VType::vtDouble:
			os << m_data.dval;
		default:
			break;
		}

		return os;
	}
}

std::ostream & operator<<(std::ostream& os, const zz::Variant& var)
{
	return var.operator<<(os);
}