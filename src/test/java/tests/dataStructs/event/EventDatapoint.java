package tests.dataStructs.event;

public class EventDatapoint {
	Long at;
	Object value;

	public EventDatapoint() {
	}

	public EventDatapoint(Long at, Object value) {
		this.at = at;
		this.value = value;
	}

	public Long getAt() {
		return at;
	}

	public void setAt(Long at) {
		this.at = at;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
}
