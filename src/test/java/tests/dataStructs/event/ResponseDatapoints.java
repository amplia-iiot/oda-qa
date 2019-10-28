package tests.dataStructs.event;

public class ResponseDatapoints {
	private Long at;
	private Object value;

	public boolean isEquals(Object value, Class c) {
		return (value.equals(c.cast(this.value)));
	}

	public void setAt(Long at) {
		this.at = at;
	}

	public void setValue(Object value) {
		this.value = value;
	}
}
