package es.amplia.oda.operation.get.dataStructs;

public class ReadResponseStruct {
	private int id;
	private int status;
	private String message;
	private long at;
	private Object value;

	public ReadResponseStruct() {
	}

	public ReadResponseStruct(int id, int status, String message, long at, Object value) {
		this.id = id;
		this.status = status;
		this.message = message;
		this.at = at;
		this.value = value;
	}

	public int getId() {
		return id;
	}

	public int getStatus() {
		return status;
	}

	public String getMessage() {
		return message;
	}

	public long getAt() {
		return at;
	}

	public Object getValue() {
		return value;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setAt(long at) {
		this.at = at;
	}

	public void setValue(Object value) {
		this.value = value;
	}
}
