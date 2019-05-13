package hellocucumber.dataStructs.update;

public class Value {
	String string;
	Array[] array;

	public Value() {
	}

	public Value(String string, Array[] array) {
		this.string = string;
		this.array = array;
	}

	public String getString() {
		return string;
	}

	public void setString(String string) {
		this.string = string;
	}

	public Array[] getArray() {
		return array;
	}

	public void setArray(Array[] array) {
		this.array = array;
	}
}
