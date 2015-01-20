// ------ Copyright -----
// by Zhengrong Zhou 2014
// ----------------------

#pragma once
#include "exportvariant.h"
#include <iostream>

namespace zz
{
	class VARIANT_API Variant
	{
	public:
		enum class VType
		{
			vtInvalid = 0,
			vtInt,
			vtDouble
		};
		explicit Variant(int val);
		explicit Variant(double val);

		~Variant();

		VType type() const { return m_type; }
		std::ostream & operator<<(std::ostream& os) const;
	private:
		union DataUnion
		{
			int ival;
			double dval;
		};

		VType m_type;
		DataUnion m_data;
	};
}

VARIANT_API std::ostream & operator<<(std::ostream& os, const zz::Variant& var);


