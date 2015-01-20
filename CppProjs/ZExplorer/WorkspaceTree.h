// ------ Copyright -----
// by Zhengrong Zhou 2014
// ----------------------
#pragma once
#include <QtWidgets/QTreeWidget>

namespace zz
{
	class WorkspaceTree : public QTreeWidget
	{
		Q_OBJECT
	public:
		~WorkspaceTree();
		static WorkspaceTree * instance();

	private:
		WorkspaceTree(QWidget * parent = nullptr);
		Q_DISABLE_COPY(WorkspaceTree);
	};
}


