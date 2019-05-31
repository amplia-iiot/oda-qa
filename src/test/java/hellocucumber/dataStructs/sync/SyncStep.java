package hellocucumber.dataStructs.sync;

import java.util.List;

public class SyncStep {
	String name;
	String result;
	String description;
	Long timestamp;
	List<SyncOutputVariable> response;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public List<SyncOutputVariable> getResponse() {
		return response;
	}

	public void setResponse(List<SyncOutputVariable> response) {
		this.response = response;
	}

	public SyncStep(String name, String result, String description, Long timestamp, List<SyncOutputVariable> response) {
		this.name = name;
		this.result = result;
		this.description = description;
		this.timestamp = timestamp;
		this.response = response;
	}

	public SyncStep() {
	}
}
