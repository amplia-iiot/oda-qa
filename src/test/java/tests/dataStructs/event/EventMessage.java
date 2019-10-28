package tests.dataStructs.event;

public class EventMessage {
	MessageDatastreams[] datastreams;

	public EventMessage() {
		this.datastreams = new MessageDatastreams[0];
	}

	public EventMessage(MessageDatastreams[] datastreams) {
		this.datastreams = datastreams;
	}

	public MessageDatastreams[] getDatastreams() {
		return datastreams;
	}

	public void setDatastreams(MessageDatastreams[] datastreams) {
		this.datastreams = datastreams;
	}

	public void addDatastream(MessageDatastreams datastreams) {
		MessageDatastreams[] temp;
		temp = this.datastreams;
		this.datastreams = new MessageDatastreams[temp.length + 1];
		System.arraycopy(temp, 0, this.datastreams, 0, temp.length);
		this.datastreams[this.datastreams.length - 1] = datastreams;
	}
}
