package hellocucumber.discover.configuration;

import hellocucumber.discover.DiscoverData;

import javax.naming.ConfigurationException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ConfigurationHandler {
	private static final String MAIN_DEVICE_ID_PROPERTY_NAME = "MAINDEVICEID";

	private DiscoverData data;

	public ConfigurationHandler(DiscoverData discoverData) {
		this.data = discoverData;
	}


	public void load() throws IOException, ConfigurationException {
		File cfg = new File("./src/test/resources/config/DiscoverData.cfg");

		Map<String,String> props = new HashMap<>();
		BufferedReader reader = new BufferedReader(new FileReader(cfg.getPath()));
		String line;

		do {
			line = reader.readLine();
			if(line != null) {
				String[] phrases = line.split("=");
				if(phrases.length == 2) {
					props.put(phrases[0], phrases[1]);
				}
				else {
					throw new ConfigurationException();
				}
			}
		} while (line != null);

		data.setMAINDEVICEID(props.get(MAIN_DEVICE_ID_PROPERTY_NAME));

		if(data.getMAINDEVICEID() == null) {
			throw new ConfigurationException();
		}
	}
}
