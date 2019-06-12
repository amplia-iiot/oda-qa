package hellocucumber.dataStructs.general;

import hellocucumber.dataStructs.event.ResponseDatastreams;

public class ResponseFormat {
	private String version;
	private Operation operation;

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

	public boolean verifyResult(String expected) {
		return this.operation.getResponse().getResultCode().equals(expected);
	}

	public boolean verifyStepResult(String expected) {
		return this.operation.getResponse().getSteps().get(0).getResponse().get(0).getResultCode().equals(expected);
	}

	public Object responseValue() {
		return this.operation.getResponse().getSteps().get(0).getResponse().get(0).getVariableValue();
	}

	public String resultDescription() {
		return this.getOperation().getResponse().getResultDescription();
	}

	public String resultStepDescription() {
		return this.operation.getResponse().getSteps().get(0).getResponse().get(0).getResultDescription();
	}

	/*public boolean isDatastream(String id) {
		for (ResponseDatastreams ds: datastreams) {
			if(ds.getId().equals(id))
				return true;
		}
		return false;
	}*/
}
