package tests.dataStructs.write;

public class WriteResponseStruct {
	int id;
	int status;
	String message;

	public WriteResponseStruct(int id, int status, String message) {
		this.id = id;
		this.status = status;
		this.message = message;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
