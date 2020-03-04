package es.amplia.oda.operation.update.dataStructs;

public class UpdateRequestStruct {
	private Operation operation;

	public UpdateRequestStruct() {
	}

	public UpdateRequestStruct(Operation operation) {
		this.operation = operation;
	}

	public Operation getOperation() {
		return operation;
	}

	public void setOperation(Operation operation) {
		this.operation = operation;
	}
}
