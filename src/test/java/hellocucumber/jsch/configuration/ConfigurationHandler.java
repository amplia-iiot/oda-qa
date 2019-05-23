package hellocucumber.jsch.configuration;

import hellocucumber.jsch.JschData;

import javax.naming.ConfigurationException;
import java.io.*;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

public class ConfigurationHandler {
	private static final String PATH_CFG_PROPERTY_NAME = "PATH_CFG";
	private static final String SSH_USER_IP_PROPERTY_NAME = "SSH_USER_IP";
	private static final String SSH_USER_USER_PROPERTY_NAME = "SSH_USER_USER";
	private static final String SSH_USER_PASSWORD_PROPERTY_NAME = "SSH_USER_PASSWORD";

	private JschData data;

	public ConfigurationHandler(JschData jschData) {
		this.data = jschData;
	}


	public void load() throws IOException, ConfigurationException {
		File cfg = new File("./src/test/resources/config/JschData.cfg");

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

		DatagramSocket socket = new DatagramSocket();
		socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
		String ip = socket.getLocalAddress().getHostAddress();

		data.setPATH_CFG(props.get(PATH_CFG_PROPERTY_NAME));
		data.setSSH_USER_IP(props.get(SSH_USER_IP_PROPERTY_NAME));
		data.setSSH_SERVER_IP(ip);
		data.setSSH_USER_USER(props.get(SSH_USER_USER_PROPERTY_NAME));
		data.setSSH_USER_PASSWORD(props.get(SSH_USER_PASSWORD_PROPERTY_NAME));

		if(data.getPATH_CFG() == null || data.getSSH_USER_IP() == null || data.getSSH_SERVER_IP() == null ||
				data.getSSH_USER_USER() == null || data.getSSH_USER_PASSWORD() == null) {
			throw new ConfigurationException();
		}
	}
}
