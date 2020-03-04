package es.amplia.oda.operation.update.dataStructs;

public class Response {
	private String id;
	private String deviceId;
	private String name;
	private String resultCode;
	private String resultDescription;
	private Step[] steps;

	public Response() {
	}

	public Response(String id, String deviceId, String name, String resultCode, String resultDescription, Step[] steps) {
		this.id = id;
		this.deviceId = deviceId;
		this.name = name;
		this.resultCode = resultCode;
		this.resultDescription = resultDescription;
		this.steps = steps;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getResultCode() {
		return resultCode;
	}

	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}

	public String getResultDescription() {
		return resultDescription;
	}

	public void setResultDescription(String resultDescription) {
		this.resultDescription = resultDescription;
	}

	public Step[] getSteps() {
		return steps;
	}

	public void setSteps(Step[] steps) {
		this.steps = steps;
	}
}
