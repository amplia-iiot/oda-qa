package tests.discover;

import tests.discover.configuration.ConfigurationHandler;

import javax.naming.ConfigurationException;
import java.io.IOException;

public class DiscoverData {
	private String MAINDEVICEID;

	public DiscoverData() throws IOException, ConfigurationException {
		ConfigurationHandler handler = new ConfigurationHandler(this);
		handler.load();
	}

	public String getMAINDEVICEID() {
		return MAINDEVICEID;
	}

	public void setMAINDEVICEID(String MAINDEVICEID) {
		this.MAINDEVICEID = MAINDEVICEID;
	}
}
