package hellocucumber.dataStructs.general;

public class ResponseFormat {
	private Operation operation;
	private String version;

	public ResponseFormat(){}

	public ResponseFormat(Operation operation, String version) {
		this.operation = operation;
		this.version = version;
	}


	public Operation getOperation() {
		return operation;
	}

	public void setOperation(Operation operation) {
		this.operation = operation;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public boolean verifyResult() {
		return this.operation.getResponse().getSteps().get(0).getResponse().get(0).getResultCode().equals("SUCCESS");
	}

	public Object responseValue() {
		return this.operation.getResponse().getSteps().get(0).getResponse().get(0).getVariableValue();
	}
}
