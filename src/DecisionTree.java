import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class DecisionTree {
	private DecisionTreeNode root;
	private String classificationAttribute;

	public DecisionTree(Dataset trainSet, int treeDepth) {
		List<String> classifications = trainSet.getClassifications();
		this.classificationAttribute = trainSet.getClassificationAttribute();

		if (classifications.size() == 1) {
			root = new DecisionTreeNodeLeaf(null, classifications.get(0));
		} else {
			root = new DecisionTreeNodeBranch(null);
			buildSubtree(trainSet, (DecisionTreeNodeBranch) root, treeDepth, 1);
		}
	}

	public void buildSubtree(Dataset subDataset, DecisionTreeNodeBranch currentNode, int treeDepth, int workingDepth) {
		var splitAttributeResult = calculateSplitAttribute(subDataset);
		currentNode.setAttribute(splitAttributeResult.getSplitAttribute());

		List<DecisionTreeNode> children = new ArrayList<>();
		for (var entry : splitAttributeResult.getValueRecordsMap().entrySet()) {
			var value = entry.getKey();
			var records = entry.getValue();

			if (workingDepth + 1 == treeDepth) {
				var bestClassification = findBestClassification(records, subDataset.getClassifications());
				var nextNode = new DecisionTreeNodeLeaf(value, bestClassification);
				children.add(nextNode);
			} else if (isAllSameClassification(records)) {
				var sameClassification = records.get(0).getClassification();
				var nextNode = new DecisionTreeNodeLeaf(value, sameClassification);
				children.add(nextNode);
			} else {
				DecisionTreeNode nextNode;
				List<String> nextAttributes = new ArrayList<>(subDataset.getAttributes());
				nextAttributes.remove(splitAttributeResult.getSplitAttribute());
				if (nextAttributes.size() == 1) {
					var bestClassification = findBestClassification(records, subDataset.getClassifications());
					nextNode = new DecisionTreeNodeLeaf(value, bestClassification);
				} else {
					nextNode = new DecisionTreeNodeBranch(value);
					var nextSubDataset = Dataset.FromRecordsWithoutAttribute(records,
							splitAttributeResult.getSplitAttribute(), subDataset.getName(), nextAttributes,
							this.classificationAttribute);
					buildSubtree(nextSubDataset, (DecisionTreeNodeBranch) nextNode, treeDepth, workingDepth + 1);
				}
				children.add(nextNode);
			}
		}

		currentNode.addChildren(children);
	}

	private CalculateSplitAttributeResult calculateSplitAttribute(Dataset dataset) {
		var minEntropy = Double.POSITIVE_INFINITY;
		String minAttribute = null;
		Map<String, List<DatasetRecord>> minMapping = null;

		for (var attribute : dataset.getAttributes()) {
			if (attribute.equals(this.classificationAttribute)) {
				continue;
			}
			var entropy = 0.0;
			Map<String, List<DatasetRecord>> mapping = new Hashtable<>();

			for (var value : dataset.getAttributeValues(attribute)) {
				var subEntropy = 0.0;
				var filteredRecords = dataset.getByAttributeValue(attribute, value);
				mapping.put(value, filteredRecords);

				for (var classification : dataset.getClassifications()) {
					var positiveCount = filteredRecords.stream()
							.filter(record -> record.getClassification().equals(classification)).count();
					if (positiveCount > 0) {
						var positiveRatio = (double) positiveCount / (double) filteredRecords.size();
						subEntropy -= positiveRatio * log2(positiveRatio);
					}
				}

				subEntropy *= ((double) filteredRecords.size() / (double) dataset.size());
				entropy += subEntropy;
			}

			if (entropy < minEntropy) {
				minEntropy = entropy;
				minAttribute = attribute;
				minMapping = mapping;
			}
		}

		return new CalculateSplitAttributeResult(minMapping, minAttribute);
	}

	private double log2(double num) {
		return Math.log(num) / Math.log(2);
	}

	private String findBestClassification(List<DatasetRecord> records, List<String> classifications) {
		Map<String, Integer> classificationCounts = new Hashtable<>();
		for (var classification : classifications) {
			classificationCounts.put(classification, 0);
		}

		for (var record : records) {
			var currentClassification = record.getClassification();
			classificationCounts.replace(currentClassification, classificationCounts.get(currentClassification) + 1);
		}

		int maxCount = -1;
		String maxClassification = null;

		for (var entry : classificationCounts.entrySet()) {
			var classification = entry.getKey();
			var count = entry.getValue();

			if (count > maxCount) {
				maxCount = count;
				maxClassification = classification;
			}
		}

		return maxClassification;
	}

	private boolean isAllSameClassification(List<DatasetRecord> records) {
		var initialClassification = records.get(0).getClassification();

		for (var record : records) {
			if (!record.getClassification().equals(initialClassification)) {
				return false;
			}
		}

		return true;
	}

	public String classify(DatasetRecord record) {
		return classifyHelper(record, root);
	}

	private String classifyHelper(DatasetRecord record, DecisionTreeNode currentNode) {
		if (currentNode instanceof DecisionTreeNodeLeaf) {
			var currentNodeLeaf = (DecisionTreeNodeLeaf) currentNode;
			return currentNodeLeaf.getClassification();
		} else if (currentNode instanceof DecisionTreeNodeBranch) {
			var currentNodeBranch = (DecisionTreeNodeBranch) currentNode;
			var nextNode = currentNodeBranch.getNextChild(record);
			return classifyHelper(record, nextNode);
		} else {
			return null;
		}
	}

	@Override
	public String toString() {
		var sb = new StringBuilder();

		toStringHelper(root, sb, "");

		return sb.toString();
	}

	private void toStringHelper(DecisionTreeNode currentNode, StringBuilder sb, String indent) {
		if (currentNode instanceof DecisionTreeNodeLeaf) {
			var currentNodeLeaf = (DecisionTreeNodeLeaf) currentNode;

			if (currentNodeLeaf.getValue() != null) {
				sb.append(indent + currentNodeLeaf.getValue() + ": " + currentNodeLeaf.getClassification() + "\n");
			} else {
				sb.append(currentNodeLeaf.getValue() + "\n");
			}
		} else {
			var currentNodeBranch = (DecisionTreeNodeBranch) currentNode;

			if (currentNodeBranch.getValue() != null) {
				sb.append(indent + currentNodeBranch.getValue() + ": " + currentNodeBranch.getAttribute() + "?\n");
			} else {
				sb.append(currentNodeBranch.getAttribute() + "?\n");
			}

			for (var child : currentNodeBranch.getChildren()) {
				toStringHelper(child, sb, indent + "|\t");
			}
		}
	}

	private class CalculateSplitAttributeResult {
		private Map<String, List<DatasetRecord>> valueRecordsMap;
		private String splitAttribute;

		private CalculateSplitAttributeResult(Map<String, List<DatasetRecord>> valueRecordsMap, String splitAttribute) {
			this.valueRecordsMap = valueRecordsMap;
			this.splitAttribute = splitAttribute;
		}

		private Map<String, List<DatasetRecord>> getValueRecordsMap() {
			return valueRecordsMap;
		}

		private String getSplitAttribute() {
			return splitAttribute;
		}
	}
}
