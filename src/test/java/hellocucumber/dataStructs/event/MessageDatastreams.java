package hellocucumber.dataStructs.event;

public class MessageDatastreams {
	String datastreamId;
	Long at;
	String value;

	public MessageDatastreams() {
	}

	public MessageDatastreams(String datastreamId, Long at, String value) {
		this.datastreamId = datastreamId;
		this.at = at;
		this.value = value;
	}

	public String getDatastreamId() {
		return datastreamId;
	}

	public void setDatastreamId(String datastreamId) {
		this.datastreamId = datastreamId;
	}

	public Long getAt() {
		return at;
	}

	public void setAt(Long at) {
		this.at = at;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
