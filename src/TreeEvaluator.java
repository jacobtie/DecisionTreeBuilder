public class TreeEvaluator {
	private DecisionTree tree;
	private Dataset testSet;
	private int correct;
	private int total;

	public static EvaluationResult evaluateTree(DecisionTree tree, Dataset testSet) {
		return new TreeEvaluator(tree, testSet).evaluate();
	}

	private TreeEvaluator(DecisionTree tree, Dataset testSet) {
		this.tree = tree;
		this.testSet = testSet;
		this.correct = 0;
		this.total = 0;
	}

	private EvaluationResult evaluate() {

		for (var record : testSet.getRecords()) {
			try {
				if (tree.classify(record).equalsIgnoreCase(record.getClassification())) {
					correct++;
				}
				total++;
			} catch (NullPointerException ex) {
				// Attribute value was not in train set, cannot classify
			}
		}

		return new EvaluationResult(total, correct);
	}

	public class EvaluationResult {
		private int total;
		private int correct;

		public EvaluationResult(int total, int correct) {
			this.total = total;
			this.correct = correct;
		}

		public int getTotal() {
			return total;
		}

		public int getCorrect() {
			return correct;
		}

		public double getAccuracy() {
			return total == 0 ? 0.0 : (double) correct / (double) total;
		}
	}
}
