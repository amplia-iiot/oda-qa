package hellocucumber.dataStructs.sync;

import java.util.List;

public class SyncResponse {
	String id;
	String deviceId;
	String name;
	String resultCode;
	String resultDescription;
	List<SyncStep> steps;

	public SyncResponse() {
	}

	public SyncResponse(String id, String deviceId, String name, String resultCode, String resultDescription, List<SyncStep> steps) {
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

	public List<SyncStep> getSteps() {
		return steps;
	}

	public void setSteps(List<SyncStep> steps) {
		this.steps = steps;
	}

	public void setId(String id) {
		this.id = id;
	}
}
