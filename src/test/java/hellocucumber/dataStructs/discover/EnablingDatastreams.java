package hellocucumber.dataStructs.discover;

public class EnablingDatastreams {
	String datastreamId;
	String mode;

	public EnablingDatastreams() {
	}

	public EnablingDatastreams(String datastreamId, String mode) {
		this.datastreamId = datastreamId;
		this.mode = mode;
	}

	public String getDatastreamId() {
		return datastreamId;
	}

	public void setDatastreamId(String datastreamId) {
		this.datastreamId = datastreamId;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}
}
