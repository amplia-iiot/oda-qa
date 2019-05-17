package hellocucumber.dataStructs.unknown;

public class UnknownResponseStruct {
	private Operation operation;
	private String version;

	public UnknownResponseStruct(){}

	public UnknownResponseStruct(Operation operation, String version) {
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

	public String resultDescription() {
		return this.operation.getResponse().getResultDescription();
	}
}
