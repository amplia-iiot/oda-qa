package es.amplia.oda.operation.localprotocoldiscovery.dataStructs;

public class Multidisable {
	DisablingDatastreams[] datastreams;

	public Multidisable() {
		datastreams = new DisablingDatastreams[0];
	}

	public Multidisable(DisablingDatastreams[] datastreams) {
		this.datastreams = datastreams;
	}

	public DisablingDatastreams[] getDatastreams() {
		return datastreams;
	}

	public void setDatastreams(DisablingDatastreams[] datastreams) {
		this.datastreams = datastreams;
	}

	public void addDatastream(DisablingDatastreams datastreams) {
		DisablingDatastreams[] temp;
		temp = this.datastreams;
		this.datastreams = new DisablingDatastreams[temp.length + 1];
		System.arraycopy(temp, 0, this.datastreams, 0, temp.length);
		this.datastreams[this.datastreams.length - 1] = datastreams;
	}
}
