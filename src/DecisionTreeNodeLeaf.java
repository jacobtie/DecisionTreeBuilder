
public class DecisionTreeNodeLeaf extends DecisionTreeNode {

	private String classification;
	
	public DecisionTreeNodeLeaf(String value, String classification) {
		super(value);
		this.classification = classification;
	}
	
	public String getClassification() {
		return classification;
	}

}
