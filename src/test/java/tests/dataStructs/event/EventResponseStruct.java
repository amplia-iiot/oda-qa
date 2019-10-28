package tests.dataStructs.event;

public class EventResponseStruct {
	String version;
	String device;
	String[] path;
	ResponseDatastreams[] datastreams;

	public String getVersion() {
		return version;
	}

	public String getDevice() {
		return device;
	}

	public String[] getPath() {
		return path;
	}

	public void setPath(String[] path) {
		this.path = path;
	}

	public ResponseDatastreams[] getDatastreams() {
		return datastreams;
	}

	public boolean constainsValue(Object value, Class c) {
		for (ResponseDatastreams ds: datastreams) {
			if(ds.containsValue(value, c))
				return true;
		}
		return false;
	}

	public boolean isDatastream(String id) {
		for (ResponseDatastreams ds: datastreams) {
			if(ds.getId().equals(id))
				return true;
		}
		return false;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public void setDatastreams(ResponseDatastreams[] datastreams) {
		this.datastreams = datastreams;
	}
}
