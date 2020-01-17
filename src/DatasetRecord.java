import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.Hashtable;

public class DatasetRecord {
	private Map<String, String> attributeValueMap;
	private int rowNum;
	private String classification;

	public DatasetRecord(Map<String, String> attributeValueMap, int rowNum, String classification) {
		this.attributeValueMap = attributeValueMap;
		this.rowNum = rowNum;
		this.classification = classification;
	}

	public DatasetRecord(List<String> attributes, String line, int rowNum) {
		this.rowNum = rowNum;
		this.attributeValueMap = new Hashtable<>();

		var values = Arrays.asList(line.split(","));

		fillAttributeValueMap(attributes, values);
		assignClassification(values);
	}

	private void fillAttributeValueMap(List<String> attributes, List<String> values) {
		for (int i = 0; i < attributes.size(); i++) {
			attributeValueMap.put(attributes.get(i), values.get(i));
		}
	}

	private void assignClassification(List<String> values) {
		classification = values.get(values.size() - 1);
	}

	public int getRowNum() {
		return rowNum;
	}

	public String get(String attribute) {
		return attributeValueMap.get(attribute);
	}

	public Map<String, String> getAttributeValueMap() {
		return attributeValueMap;
	}

	public String getClassification() {
		return classification;
	}

	public DatasetRecord cloneWithoutAttribute(String attribute) {
		Map<String, String> updatedMap = new Hashtable<>();

		for (var entry : this.attributeValueMap.entrySet()) {
			if (!entry.getKey().equals(attribute)) {
				updatedMap.put(entry.getKey(), entry.getValue());
			}
		}

		return new DatasetRecord(updatedMap, this.rowNum, this.classification);
	}
}
