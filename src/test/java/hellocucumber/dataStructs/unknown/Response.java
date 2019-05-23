package hellocucumber.dataStructs.unknown;

import hellocucumber.dataStructs.general.Step;

import java.util.ArrayList;

public class Response {
	private String id;
	private String deviceId;
	private String name;
	private String resultCode;
	private String resultDescription;

	public Response() {
	}

	public Response(String id, String deviceId, String name, String resultCode, String resultDescription, ArrayList<Step> steps) {
		this.id = id;
		this.deviceId = deviceId;
		this.name = name;
		this.resultCode = resultCode;
		this.resultDescription = resultDescription;
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
