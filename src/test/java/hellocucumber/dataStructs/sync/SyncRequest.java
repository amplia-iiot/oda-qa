package hellocucumber.dataStructs.sync;

public class SyncRequest {
	SyncResponse response;

	public SyncRequest(SyncResponse response) {
		this.response = response;
	}

	public SyncResponse getResponse() {
		return response;
	}

	public void setResponse(SyncResponse response) {
		this.response = response;
	}

	public SyncRequest() {
	}
}
