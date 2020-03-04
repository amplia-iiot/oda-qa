package utils.dataStructs;

import java.util.ArrayList;

public class Step {
	private String name;
	private String result;
	private String description;
	private long timestamp;
	private ArrayList<Resp> response;

	public Step(){}


	public Step(String name, String result, String description) {
		this.name = name;
		this.result = result;
		this.description = description;
		this.timestamp = System.currentTimeMillis();
		this.response = new ArrayList<>();
	}

	public Step(String name, String result, String description, long timestamp, ArrayList<Resp> response) {
		this.name = name;
		this.result = result;
		this.description = description;
		this.timestamp = timestamp;
		this.response = response;
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

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public ArrayList<Resp> getResponse() {
		return response;
	}

	public void setResponse(ArrayList<Resp> response) {
		this.response = response;
	}
}
