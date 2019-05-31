package hellocucumber.dataStructs.sync;

public class SyncOutputVariable {
	String variableName;
	Object variableValue;
	String resultCode;
	String resultDescription;

	public String getVariableName() {
		return variableName;
	}

	public void setVariableName(String variableName) {
		this.variableName = variableName;
	}

	public SyncOutputVariable() {
	}

	public SyncOutputVariable(String variableName, Object variableValue, String resultCode, String resultDescription) {
		this.variableName = variableName;
		this.variableValue = variableValue;
		this.resultCode = resultCode;
		this.resultDescription = resultDescription;
	}
}
