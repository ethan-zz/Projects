// ------ Copyright -----
// by Zhengrong Zhou
// ----------------------
#include "WorkspaceTree.h"

namespace zz
{
	WorkspaceTree * WorkspaceTree::instance()
	{
		static WorkspaceTree * s_me = new WorkspaceTree;
		return s_me;
	}
	WorkspaceTree::WorkspaceTree(QWidget * parent)
		: QTreeWidget(parent)
	{
	}

	WorkspaceTree::~WorkspaceTree()
	{
	}

}

