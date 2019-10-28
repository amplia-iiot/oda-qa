package tests.dataStructs.update;

public class Parameter {
	private String name;
	private String type;
	private Value value;

	public Parameter() {
	}

	public Parameter(String name, String type, Value value) {
		this.name = name;
		this.type = type;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Value getValue() {
		return value;
	}

	public void setValue(Value value) {
		this.value = value;
	}
}
