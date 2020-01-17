import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Dataset {
	private String name;
	private List<String> attributes;
	private List<DatasetRecord> records;
	private List<String> classifications;
	private String classificationAttribute;

	public Dataset(String name, List<String> attributes, List<DatasetRecord> records, String classificationAttribute) {
		this.name = name;
		this.attributes = attributes;
		this.records = records;
		this.classificationAttribute = classificationAttribute;
		initializeClassifications();
	}

	public static Dataset FromRecordsWithoutAttribute(List<DatasetRecord> records, String attribute, String name, List<String> attributes, String classificationAttribute) {
		var clonedRecords = records.stream().map(record -> record.cloneWithoutAttribute(attribute))
				.collect(Collectors.toList());
		
		return new Dataset(name, attributes, clonedRecords, classificationAttribute);
	}

	public static Dataset fromCSV(File csvFile) {
		try (var reader = new BufferedReader(new FileReader(csvFile))) {
			return new Dataset(csvFile.getName(), reader);
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
			return null;
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	private Dataset(String name, BufferedReader reader) throws IOException {
		this.name = name;
		initializeAttributes(reader);
		initializeRecords(reader);
		initializeClassifications();
	}

	private void initializeAttributes(BufferedReader reader) throws IOException {
		var firstLine = reader.readLine();
		this.attributes = Arrays.asList(firstLine.split(","));
		this.classificationAttribute = this.attributes.get(this.attributes.size() - 1);
	}

	private void initializeRecords(BufferedReader reader) throws IOException {
		this.records = new ArrayList<>();
		var line = "";
		var rowNum = 1;
		while ((line = reader.readLine()) != null) {
			this.records.add(new DatasetRecord(this.attributes, line, rowNum));
			rowNum++;
		}
	}

	private void initializeClassifications() {
		this.classifications = this.records.stream().map(record -> record.getClassification())
				.collect(Collectors.toList());
	}

	public String getName() {
		return this.name;
	}

	public int size() {
		return this.records.size();
	}

	public List<DatasetRecord> getRecords() {
		return this.records;
	}

	public List<String> getAttributes() {
		return this.attributes;
	}

	public List<String> getAttributeValues(String attribute) {
		List<String> attributeValues = new ArrayList<String>();

		for (var record : this.records) {
			var value = record.get(attribute);
			if (!attributeValues.contains(value)) {
				attributeValues.add(value);
			}
		}

		return attributeValues;
	}

	public DatasetRecord get(int index) {
		return this.records.get(index);
	}

	public List<DatasetRecord> getByAttributeValue(String attribute, String value) {
		return this.records.stream().filter(record -> record.get(attribute).equals(value)).collect(Collectors.toList());
	}

	public TrainTestDatasetPartition partition(double trainPercentage) {
		return new TrainTestDatasetPartition(this, trainPercentage);
	}

	public List<String> getClassifications() {
		return this.classifications;
	}

	public String getClassificationAttribute() {
		return this.classificationAttribute;
	}
}
