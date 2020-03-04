package es.amplia.oda.connector.dnp3.configuration;

import javax.naming.ConfigurationException;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class DNP3ConfigurationHandler {
	static final String CHANNEL_IDENTIFIER_PROPERTY_NAME = "channelIdentifier";
	static final String IP_ADDRESS_PROPERTY_NAME = "ipAddress";
	static final String IP_PORT_PROPERTY_NAME = "ipPort";
	static final String LOCAL_DEVICE_DNP_ADDRESS_PROPERTY_NAME = "localDeviceDnpAddress";
	static final String REMOTE_DEVICE_DNP_ADDRESS_PROPERTY_NAME = "remoteDeviceDnpAddress";
	static final String UNSOLICITED_RESPONSE_PROPERTY_NAME = "unsolicitedResponse";
	static final String EVENT_BUFFER_SIZE_PROPERTY_NAME = "eventBufferSize";
	static final String LOG_LEVEL_PROPERTY_NAME = "logLevel";
	static final String ENABLE_PROPERTY_NAME = "enable";

	private DNP3Configuration config;

	public DNP3ConfigurationHandler(DNP3Configuration dnp3Configuration) {
		this.config = dnp3Configuration;
	}

	public void load() throws IOException, ConfigurationException {
		File cfg = new File("./src/test/resources/config/DNP3MasterData.cfg");

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

		config.setChannelIdentifier(props.getOrDefault(CHANNEL_IDENTIFIER_PROPERTY_NAME, DNP3Configuration.DEFAULT_CHANNEL_IDENTIFIER));
		config.setIpAddress(props.getOrDefault(IP_ADDRESS_PROPERTY_NAME, DNP3Configuration.DEFAULT_IP_ADDRESS));
		config.setIpPort(Integer.parseInt(props.getOrDefault(IP_PORT_PROPERTY_NAME, String.valueOf(DNP3Configuration.DEFAULT_IP_PORT))));
		config.setLocalDeviceDNP3Address(Integer.parseInt(props.getOrDefault(LOCAL_DEVICE_DNP_ADDRESS_PROPERTY_NAME, String.valueOf(DNP3Configuration.DEFAULT_LOCAL_DEVICE_DNP3_ADDRESS))));
		config.setRemoteDeviceDNP3Address(Integer.parseInt(props.getOrDefault(REMOTE_DEVICE_DNP_ADDRESS_PROPERTY_NAME, String.valueOf(DNP3Configuration.DEFAULT_REMOTE_DEVICE_DNP3_ADDRESS))));
		config.setUnsolicitedResponse(Boolean.parseBoolean(props.getOrDefault(UNSOLICITED_RESPONSE_PROPERTY_NAME, String.valueOf(DNP3Configuration.DEFAULT_UNSOLICITED_RESPONSE))));
		config.setEventBufferSize(Integer.parseInt(props.getOrDefault(EVENT_BUFFER_SIZE_PROPERTY_NAME, String.valueOf(DNP3Configuration.DEFAULT_EVENT_BUFFER_SIZE))));
		config.setLogLevel(Integer.parseInt(props.getOrDefault(LOG_LEVEL_PROPERTY_NAME, String.valueOf(DNP3Configuration.DEFAULT_LOG_LEVEL))));
		config.setEnable(Boolean.parseBoolean(props.getOrDefault(ENABLE_PROPERTY_NAME, String.valueOf(DNP3Configuration.DEFAULT_ENABLE))));
	}
}
