public abstract class DecisionTreeNode {
	private String value;

	public DecisionTreeNode(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}

	public boolean isCorrectBranch(String otherValue) {
		return this.value.equals(otherValue);
	}
}
