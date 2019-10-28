package tests.dataStructs.update;

public class Step {
	private String name;
	private String result;
	private String description;
	private Long timestamp;

	public Step() {
	}

	public Step(String name, String result, String description, Long timestamp) {
		this.name = name;
		this.result = result;
		this.description = description;
		this.timestamp = timestamp;
	}

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
}
