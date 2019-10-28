package tests.dataStructs.general;

public class Operation {
	private Response response;

	public Operation(){}

	public Operation(Response response) {
		this.response = response;
	}

	public Response getResponse() {
		return response;
	}

	public void setResponse(Response response) {
		this.response = response;
	}
}
