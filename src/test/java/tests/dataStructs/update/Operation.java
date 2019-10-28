package tests.dataStructs.update;

public class Operation {
	private Request request;
	private Response response;

	public Operation() {
	}

	public Operation(Request request, Response response) {
		this.request = request;
		this.response = response;
	}

	public Request getRequest() {
		return request;
	}

	public void setRequest(Request request) {
		this.request = request;
	}

	public Response getResponse() {
		return response;
	}

	public void setResponse(Response response) {
		this.response = response;
	}
}
