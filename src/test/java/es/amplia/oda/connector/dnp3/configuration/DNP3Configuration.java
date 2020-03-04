package es.amplia.oda.connector.dnp3.configuration;

import com.automatak.dnp3.LogMasks;
import lombok.Data;

import javax.naming.ConfigurationException;
import java.io.IOException;

@Data
public class DNP3Configuration {
	public static final String DEFAULT_CHANNEL_IDENTIFIER = "tcpServerChannel";
	public static final String DEFAULT_IP_ADDRESS = "0.0.0.0";
	public static final int DEFAULT_IP_PORT = 20000;
	public static final int DEFAULT_LOCAL_DEVICE_DNP3_ADDRESS = 1024;
	public static final int DEFAULT_REMOTE_DEVICE_DNP3_ADDRESS = 1;
	public static final boolean DEFAULT_UNSOLICITED_RESPONSE = false;
	public static final int DEFAULT_EVENT_BUFFER_SIZE = 5;
	public static final int DEFAULT_LOG_LEVEL = LogMasks.NORMAL;
	public static final boolean DEFAULT_ENABLE = false;

	public String channelIdentifier = DEFAULT_CHANNEL_IDENTIFIER;
	public String ipAddress = DEFAULT_IP_ADDRESS;
	public int ipPort = DEFAULT_IP_PORT;
	public int localDeviceDNP3Address = DEFAULT_LOCAL_DEVICE_DNP3_ADDRESS;
	public int remoteDeviceDNP3Address = DEFAULT_REMOTE_DEVICE_DNP3_ADDRESS;
	public boolean unsolicitedResponse = DEFAULT_UNSOLICITED_RESPONSE;
	public int eventBufferSize = DEFAULT_EVENT_BUFFER_SIZE;
	public int logLevel = DEFAULT_LOG_LEVEL;
	public boolean enable = DEFAULT_ENABLE;

	public DNP3Configuration() throws IOException, ConfigurationException {
		DNP3ConfigurationHandler handler = new DNP3ConfigurationHandler(this);
		handler.load();
	}
}