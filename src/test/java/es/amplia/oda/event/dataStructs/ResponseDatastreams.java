package es.amplia.oda.event.dataStructs;

public class ResponseDatastreams {
	String id;
	ResponseDatapoints[] datapoints;

	public boolean containsValue(Object value, Class c) {
		for (ResponseDatapoints dp: datapoints) {
			if(dp.isEquals(value, c))
				return true;
		}
		return false;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setDatapoints(ResponseDatapoints[] datapoints) {
		this.datapoints = datapoints;
	}

	public String getId() {
		return id;
	}

	public ResponseDatapoints[] getDatapoints() {
		return datapoints;
	}
}
