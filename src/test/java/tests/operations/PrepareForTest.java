package tests.operations;

import com.jcraft.jsch.JSchException;
import cucumber.api.java.Before;
import tests.jsch.CopyFile;
import tests.jsch.JschData;

import javax.naming.ConfigurationException;
import java.io.*;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class PrepareForTest {
	private final String CONNECTOR_FILE_NAME = "es.amplia.oda.connector.mqtt.cfg";
	private final String DATASTREAMS_FILE_NAME = "es.amplia.oda.datastreams.mqtt.cfg";
	private final String PATH_TEMP_ODA_CONFIGURATION = "./src/test/resources/config/temp.cfg";

	private static boolean pointed = false;

	@Before
	public void pointOdaToMe() throws IOException, ConfigurationException, JSchException {
		JschData jschData = new JschData();
		if(!pointed && jschData.getPREPARE_FOR_TEST()) {
			if(jschData.getSSH_USER_IP().equals("localhost")) {
				BufferedReader file = new BufferedReader(new FileReader(jschData.getPATH_CFG() + CONNECTOR_FILE_NAME));
				StringBuilder inputBuffer = new StringBuilder();
				String line;

				while ((line = file.readLine()) != null) {
					if (line.contains("host=")) {
						inputBuffer.append("host=").append("localhost").append("\n");
					} else {
						inputBuffer.append(line).append("\n");
					}
				}
				file.close();
				FileOutputStream fileOut = new FileOutputStream(jschData.getPATH_CFG() + CONNECTOR_FILE_NAME);
				fileOut.write(inputBuffer.toString().getBytes());
				fileOut.close();

				file = new BufferedReader(new FileReader(jschData.getPATH_CFG() + DATASTREAMS_FILE_NAME));
				inputBuffer = new StringBuilder();

				while ((line = file.readLine()) != null) {
					if (line.contains("brokerURI=")) {
						inputBuffer.append("brokerURI=tcp://").append("localhost").append(":1883\n");
					} else {
						inputBuffer.append(line).append("\n");
					}
				}
				file.close();
				fileOut = new FileOutputStream(jschData.getPATH_CFG() + DATASTREAMS_FILE_NAME);
				fileOut.write(inputBuffer.toString().getBytes());
				fileOut.close();
			} else {
				DatagramSocket socket = new DatagramSocket();
				socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
				String myIp = socket.getLocalAddress().getHostAddress();

				CopyFile cf = new CopyFile(jschData.getSSH_USER_USER(), jschData.getSSH_USER_IP());

				cf.remoteToLocal(jschData.getPATH_CFG() + CONNECTOR_FILE_NAME, PATH_TEMP_ODA_CONFIGURATION);

				BufferedReader file = new BufferedReader(new FileReader(PATH_TEMP_ODA_CONFIGURATION));
				StringBuilder inputBuffer = new StringBuilder();
				String line;

				while ((line = file.readLine()) != null) {
					if (line.contains("host=")) {
						inputBuffer.append("host=").append(myIp).append("\n");
					} else {
						inputBuffer.append(line).append("\n");
					}
				}
				file.close();
				FileOutputStream fileOut = new FileOutputStream(PATH_TEMP_ODA_CONFIGURATION);
				fileOut.write(inputBuffer.toString().getBytes());
				fileOut.close();

				cf.localToRemote(PATH_TEMP_ODA_CONFIGURATION, jschData.getPATH_CFG() + CONNECTOR_FILE_NAME);

				cf.remoteToLocal(jschData.getPATH_CFG() + DATASTREAMS_FILE_NAME, PATH_TEMP_ODA_CONFIGURATION);

				file = new BufferedReader(new FileReader(PATH_TEMP_ODA_CONFIGURATION));
				inputBuffer = new StringBuilder();

				while ((line = file.readLine()) != null) {
					if (line.contains("brokerURI=")) {
						inputBuffer.append("brokerURI=tcp://").append(myIp).append(":1883\n");
					} else {
						inputBuffer.append(line).append("\n");
					}
				}
				file.close();
				fileOut = new FileOutputStream(PATH_TEMP_ODA_CONFIGURATION);
				fileOut.write(inputBuffer.toString().getBytes());
				fileOut.close();

				cf.localToRemote(PATH_TEMP_ODA_CONFIGURATION, jschData.getPATH_CFG() + DATASTREAMS_FILE_NAME);
			}

			File toDelete = new File(PATH_TEMP_ODA_CONFIGURATION);
			toDelete.delete();
			pointed = true;
		}
	}
}
