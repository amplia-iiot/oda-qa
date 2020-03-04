package utils.dataStructs;

public class Resp {
	private String variableName;
	private Object variableValue;
	private String resultCode;
	private String resultDescription;

	public Resp(String variableName, Object variableValue, String resultCode, String resultDescription) {
		this.variableName = variableName;
		this.variableValue = variableValue;
		this.resultCode = resultCode;
		this.resultDescription = resultDescription;
	}

	public Resp() {
	}

	public String getVariableName() {
		return variableName;
	}

	public void setVariableName(String variableName) {
		this.variableName = variableName;
	}

	public String getResultCode() {
		return resultCode;
	}

	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}

	public String getResultDescription() {
		return resultDescription;
	}

	public void setResultDescription(String resultDescription) {
		this.resultDescription = resultDescription;
	}

	public Object getVariableValue() {
		return variableValue;
	}

	public void setVariableValue(Object variableValue) {
		this.variableValue = variableValue;
	}
}
