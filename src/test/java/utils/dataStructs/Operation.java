package utils.dataStructs;

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
