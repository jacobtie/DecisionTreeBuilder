import java.util.ArrayList;
import java.util.List;

public class DecisionTreeNodeBranch extends DecisionTreeNode {
	private String attribute;
	private List<DecisionTreeNode> children;
	
	public DecisionTreeNodeBranch(String value) {
		super(value);
		children = new ArrayList<>();
	}
	
	public String getAttribute() {
		return attribute;
	}
	
	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	public List<DecisionTreeNode> getChildren() {
		return children;
	}
	
	public void addChildren(List<DecisionTreeNode> children) {
		this.children.addAll(children);
	}
	
	public DecisionTreeNode getNextChild(DatasetRecord record) {
		DecisionTreeNode nextChild = null;
		for (var node : children) {
			if (node.isCorrectBranch(record.get(attribute))) {
				nextChild = node;
				break;
			}
		}
		
		return nextChild;
	}
}
