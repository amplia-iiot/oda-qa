package hellocucumber;

import com.jcraft.jsch.JSchException;
import cucumber.api.java.Before;
import hellocucumber.jsch.CopyFile;
import hellocucumber.jsch.JschData;

import javax.naming.ConfigurationException;
import java.io.*;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class PrepareForTest {
	private final String CONNECTOR_FILE_NAME = "es.amplia.oda.connector.mqtt.cfg";
	private final String DATASTREAMS_FILE_NAME = "es.amplia.oda.datastreams.mqtt.cfg";
	private final String PATH_TEMP_ODA_CONFIGURATION = "./src/test/resources/config/temp.cfg";

	@Before
	public void pointOdaToMe() throws IOException, ConfigurationException, JSchException {
		DatagramSocket socket = new DatagramSocket();
		socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
		String myIp = socket.getLocalAddress().getHostAddress();

		JschData jschData = new JschData();
		CopyFile cf = new CopyFile(jschData.getSSH_USER_USER(), jschData.getSSH_USER_IP());

		cf.remoteToLocal(jschData.getPATH_CFG()+CONNECTOR_FILE_NAME, PATH_TEMP_ODA_CONFIGURATION);

		BufferedReader file = new BufferedReader(new FileReader(PATH_TEMP_ODA_CONFIGURATION));
		StringBuilder inputBuffer = new StringBuilder();
		String line;

		while ((line = file.readLine()) != null) {
			if(line.contains("host=")) {
				inputBuffer.append("host=").append(myIp).append("\n");
			} else {
				inputBuffer.append(line).append("\n");
			}
		}
		file.close();
		FileOutputStream fileOut = new FileOutputStream(PATH_TEMP_ODA_CONFIGURATION);
		fileOut.write(inputBuffer.toString().getBytes());
		fileOut.close();

		cf.localToRemote(PATH_TEMP_ODA_CONFIGURATION, jschData.getPATH_CFG()+CONNECTOR_FILE_NAME);

		cf.remoteToLocal(jschData.getPATH_CFG()+DATASTREAMS_FILE_NAME, PATH_TEMP_ODA_CONFIGURATION);

		file = new BufferedReader(new FileReader(PATH_TEMP_ODA_CONFIGURATION));
		inputBuffer = new StringBuilder();

		while ((line = file.readLine()) != null) {
			if(line.contains("brokerURI=")) {
				inputBuffer.append("brokerURI=tcp://").append(myIp).append(":1883\n");
			} else {
				inputBuffer.append(line).append("\n");
			}
		}
		file.close();
		fileOut = new FileOutputStream(PATH_TEMP_ODA_CONFIGURATION);
		fileOut.write(inputBuffer.toString().getBytes());
		fileOut.close();

		cf.localToRemote(PATH_TEMP_ODA_CONFIGURATION, jschData.getPATH_CFG()+DATASTREAMS_FILE_NAME);

		File toDelete = new File(PATH_TEMP_ODA_CONFIGURATION);
		toDelete.delete();
	}
}
