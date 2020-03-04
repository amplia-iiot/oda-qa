package es.amplia.oda.operation.update.dataStructs;

public class Request {
	private Long timestamp;
	private String name;
	private Parameter[] parameters;
	private String id;

	public Request() {
	}

	public Request(Long timestamp, String name, Parameter[] parameters, String id) {
		this.timestamp = timestamp;
		this.name = name;
		this.parameters = parameters;
		this.id = id;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Parameter[] getParameters() {
		return parameters;
	}

	public void setParameters(Parameter[] parameters) {
		this.parameters = parameters;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
