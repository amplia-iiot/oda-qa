package es.amplia.oda.operation.update.dataStructs;

public class UpdateResponseStruct {
	private String version;
	private Operation operation;

	public UpdateResponseStruct() {
	}

	public UpdateResponseStruct(String version, Operation operation) {
		this.version = version;
		this.operation = operation;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Operation getOperation() {
		return operation;
	}

	public void setOperation(Operation operation) {
		this.operation = operation;
	}

	public String getResultResponse() {
		return this.operation.getResponse().getResultCode();
	}
}
