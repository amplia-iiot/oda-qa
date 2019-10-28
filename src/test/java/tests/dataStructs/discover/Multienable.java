package tests.dataStructs.discover;

public class Multienable {
	EnablingDatastreams[] datastreams;

	public Multienable() {
		datastreams = new EnablingDatastreams[0];
	}

	public Multienable(EnablingDatastreams[] datastreams) {
		this.datastreams = datastreams;
	}

	public EnablingDatastreams[] getDatastreams() {
		return datastreams;
	}

	public void setDatastreams(EnablingDatastreams[] datastreams) {
		this.datastreams = datastreams;
	}

	public void addDatastream(EnablingDatastreams datastreams) {
		EnablingDatastreams[] temp;
		temp = this.datastreams;
		this.datastreams = new EnablingDatastreams[temp.length + 1];
		System.arraycopy(temp, 0, this.datastreams, 0, temp.length);
		this.datastreams[this.datastreams.length - 1] = datastreams;
	}
}
