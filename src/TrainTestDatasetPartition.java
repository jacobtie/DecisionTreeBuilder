import java.util.List;
import java.util.Random;
import java.util.ArrayList;

public class TrainTestDatasetPartition {
	private Dataset trainSet;
	private Dataset testSet;

	public TrainTestDatasetPartition(Dataset dataset, double trainPercentage) {
		var trainSize = (int) Math.ceil((double) dataset.size() * trainPercentage);

		List<Integer> trainIndices = calculateTrainIndices(trainSize, dataset.size());

		calculateTrainTestSets(trainIndices, dataset);
	}
	
	private List<Integer> calculateTrainIndices(int trainSize, int datasetSize) {
		var rand = new Random();
		List<Integer> trainIndices = new ArrayList<>(trainSize);

		for (int i = 0; i < trainSize; i++) {
			var randIndex = -1;
			do {
				randIndex = rand.nextInt(datasetSize);
			} while (trainIndices.contains(randIndex));
			trainIndices.add(randIndex);
		}
		
		return trainIndices;
	}
	
	private void calculateTrainTestSets(List<Integer> trainIndices, Dataset dataset) {
		List<DatasetRecord> trainRecords = new ArrayList<>();
		List<DatasetRecord> testRecords = new ArrayList<>();
		for (int i = 0; i < dataset.size(); i++) {
			if (trainIndices.contains(i)) {
				trainRecords.add(dataset.get(i));
			} else {
				testRecords.add(dataset.get(i));
			}
		}

		trainSet = new Dataset(dataset.getName(), dataset.getAttributes(), trainRecords, dataset.getClassificationAttribute());
		testSet = new Dataset(dataset.getName(), dataset.getAttributes(), testRecords, dataset.getClassificationAttribute());
	}

	public Dataset getTrainSet() {
		return trainSet;
	}

	public Dataset getTestSet() {
		return testSet;
	}
}

