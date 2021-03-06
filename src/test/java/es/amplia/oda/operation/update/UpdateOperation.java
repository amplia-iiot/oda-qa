package es.amplia.oda.operation.update;

import com.jcraft.jsch.JSchException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import utils.dataStructs.ResponseFormat;
import utils.discover.DiscoverData;
import utils.http.HttpUtils;
import utils.jsch.JschData;
import utils.jsch.CopyFile;
import utils.serializer.SerializerJSON;
import org.apache.commons.io.FileUtils;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import javax.naming.ConfigurationException;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

public class UpdateOperation {

	private MqttClient client = new MqttClient("tcp://localhost", "ClientSet", new MemoryPersistence());
	private static final String MESSAGE_COMMUNICATION_SUCCESS = "SUCCESSFUL";
	private static final int PORT_HTTP_SERVER = 9000;
	private HttpServer server;
	private static final String PATH_TO_LOCAL_CFG = "./src/test/resources/es/amplia/oda/operations/es.amplia.oda.datastreams.mqtt.cfg";
	private static final String PATH_TO_LOCAL_RULE = "./src/test/resources/es/amplia/oda/operations/";
	private CopyFile cf;
	private final JschData jschData = new JschData();
	private final DiscoverData discoverData = new DiscoverData();
	private final String CONFIG_FILE_NAME = "es.amplia.oda.datastreams.mqtt.cfg";

	private boolean responseReceived;
	private boolean responseIsOk;
	private String name;
	private String datastreamId;

	public UpdateOperation() throws MqttException, IOException, ConfigurationException {
		// This method is unimplemented because we need put a exception for the MqttClient
	}

	@Given("a {string} for the rule we want to create")
	public void aForTheRuleWeWantToCreate(String name) {
		this.name = name;
	}

	@Given("the {string} of the datastream we want to create")
	public void theOfTheDatastreamWeWantToCreate(String id) {
		this.datastreamId = id;
	}

	@When("I send a request to ODA to change the configuration")
	public void iSendARequestToODAToChangeTheConfiguration() throws MqttException, IOException, JSchException, ConfigurationException {
		this.client.connect();
		cf = new CopyFile(jschData.getSSH_USER_USER(), jschData.getSSH_USER_IP());

		this.client.setCallback(new TestCallback());
		this.client.subscribe("odm/response/#");

		this.responseIsOk = false;
		this.responseReceived = false;

		server = HttpServer.create(new InetSocketAddress(PORT_HTTP_SERVER), 0);
		server.createContext("/", new RootHandler());
		server.createContext("/echoHeader", new EchoHeaderHandler());
		server.createContext("/echoGet", new EchoGetHandlerConfig());
		server.createContext("/echoPost", new EchoPostHandler());
		server.setExecutor(null);
		server.start();

		cf.remoteToLocal(jschData.getPATH_CFG()+CONFIG_FILE_NAME, "./src/test/resources/temp.cfg");

		String temp = "{\"operation\":{\"request\":{\"timestamp\":1557395219834,\"name\":\"UPDATE\",\"parameters\":[{" +
				"\"name\":\"bundleName\",\"value\":{\"string\":\"oda-smart-energy-test\"}},{\"name\":\"bundleVersion\"," +
				"\"value\":{\"string\":\"2.0.0\"}},{\"name\":\"deploymentElements\",\"type\":\"string\",\"value\":{" +
				"\"array\":[{\"name\":\"es.amplia.oda.datastreams.mqtt\",\"version\":\"2.0.0\",\"type\":\"CONFIGURATION" +
				"\",\"downloadUrl\":\"http://" + jschData.getSSH_SERVER_IP() + ":" + PORT_HTTP_SERVER + "/echoGet" +
				"\",\"path\":\"configuration\",\"order\":1,\"operation\":\"UPGRADE\",\"validators\":[],\"size\":334,\"" +
				"oldVersion\":\"1.0.0\",\"oldName\":\"es.amplia.oda.datastreams.mqtt\",\"oldPath\":\"configuration\"" +
				"}]}}],\"id\":\"48589c6e-3d9f-4e59-a066-81f357fb6cf8\"}}}";
		client.publish("odm/request/" + discoverData.getMAINDEVICEID(), new MqttMessage(temp.getBytes()));
	}

	@When("I send a request to ODA to create the rule")
	public void iSendARequestToODAToCreateTheRule() throws MqttException, JSchException, ConfigurationException, IOException {
		this.client.connect();
		cf = new CopyFile(jschData.getSSH_USER_USER(), jschData.getSSH_USER_IP());

		this.client.setCallback(new TestCallback());
		this.client.subscribe("odm/response/#");

		this.responseIsOk = false;
		this.responseReceived = false;

		server = HttpServer.create(new InetSocketAddress(PORT_HTTP_SERVER), 0);
		server.createContext("/", new RootHandler());
		server.createContext("/echoHeader", new EchoHeaderHandler());
		server.createContext("/echoGet", new EchoGetHandlerRule());
		server.createContext("/echoPost", new EchoPostHandler());
		server.setExecutor(null);
		server.start();

		String temp = "{\"operation\":{\"request\":{\"timestamp\":1557395219834,\"name\":\"UPDATE\",\"parameters\":[{" +
				"\"name\":\"bundleName\",\"value\":{\"string\":\"rules-creation-test\"}},{\"name\":\"bundleVersion\"," +
				"\"value\":{\"string\":\"2.0.0\"}},{\"name\":\"deploymentElements\",\"type\":\"string\",\"value\":{" +
				"\"array\":[{\"name\":\"" + this.name + "\",\"version\":\"2.0.0\",\"type\":\"SOFTWARE" +
				"\",\"downloadUrl\":\"http://" + jschData.getSSH_SERVER_IP() + ":" + PORT_HTTP_SERVER + "/echoGet" +
				"\",\"path\":\"rules/" + this.datastreamId + "\",\"order\":1,\"operation\":\"INSTALL\",\"validators\":[]," +
				"\"size\":334,\"oldVersion\":\"1.0.0\"}]}}],\"id\":\"48589c6e-3d9f-4e59-a066-81f357fb6cf8\"}}}";
		client.publish("odm/request/" + discoverData.getMAINDEVICEID(), new MqttMessage(temp.getBytes()));
	}

	@Then("the new configuration is the same that the file")
	public void theNewConfigurationIsTheSameThatTheFile() throws InterruptedException, MqttException, IOException, JSchException {
		for(int i = 0; i < 10 && !responseReceived; i++) {
			TimeUnit.MILLISECONDS.sleep(500);
		}
		cf.remoteToLocal(jschData.getPATH_CFG()+CONFIG_FILE_NAME, "./src/test/resources/comparing.cfg");
		File comparing = new File("./src/test/resources/comparing.cfg");
		File temp = new File("./src/test/resources/temp.cfg");
		boolean areFileOk =  FileUtils.contentEquals(comparing, new File("./src/test/resources/es/amplia/oda/operations/es.amplia.oda.datastreams.mqtt.cfg"));
		boolean deletedTempFiles = false;
		cf.localToRemote("./src/test/resources/temp.cfg", jschData.getPATH_CFG()+CONFIG_FILE_NAME);
		if(comparing.delete() && temp.delete()) {
			deletedTempFiles = true;
		}
		server.stop(0);
		client.disconnect();
		cf.disconnect();
		assertTrue(areFileOk);
		assertTrue(deletedTempFiles);
		assertTrue(responseIsOk);
	}

	@Then("the new rule is the same that the file")
	public void theNewRuleIsTheSameThatTheFile() throws IOException, JSchException, InterruptedException, MqttException {
		for(int i = 0; i < 10 && !responseReceived; i++) {
			TimeUnit.MILLISECONDS.sleep(1000);
		}
		cf.remoteToLocal(jschData.getPATH_RLS() + datastreamId + FileSystems.getDefault().getSeparator() + name + ".js", "./src/test/resources/comparing.js");
		File comparing = new File("./src/test/resources/comparing.js");
		File compared = new File("./src/test/resources/es/amplia/oda/operations/" + name + ".js");
		FileReader comparingReader = new FileReader(comparing);
		FileReader comparedReader = new FileReader(compared);
		BufferedReader bufferedComparing = new BufferedReader(comparingReader);
		BufferedReader bufferedCompared = new BufferedReader(comparedReader);
		boolean areFileOk = true;
		bufferedComparing.readLine();
		bufferedComparing.readLine();
		String line = "";

		while((line = bufferedComparing.readLine()) != null) {
			if(!line.equals(bufferedCompared.readLine())) {
				areFileOk = false;
			}
		}

		comparing.delete();
		File remoteFile = new File(jschData.getPATH_RLS() + datastreamId + FileSystems.getDefault().getSeparator() + name + ".js");
		//remoteFile.delete();
		server.stop(0);
		client.disconnect();
		cf.disconnect();
		assertTrue(areFileOk);
		assertTrue(responseIsOk);
	}

	public class TestCallback implements MqttCallback {
		@Override
		public void connectionLost(Throwable throwable) {/* method not used*/}
		@Override
		public void messageArrived(String topic, MqttMessage message) throws IOException {
			ResponseFormat response = SerializerJSON.deserialize(message.getPayload(), ResponseFormat.class);
			if(response.verifyResult(MESSAGE_COMMUNICATION_SUCCESS)) {
				responseIsOk = true;
			}
			responseReceived = true;
		}
		@Override
		public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {/* method not used*/}
	}

	private class RootHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange httpExchange) {
			/*method not used*/
		}
	}

	private class EchoHeaderHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange httpExchange) {
			/*method not used*/
		}
	}

	private class EchoGetHandlerConfig implements HttpHandler {
		@Override
		public void handle(HttpExchange httpExchange) throws IOException {
			Map<String, Object> parameters = new HashMap<>();
			URI requestedUri = httpExchange.getRequestURI();
			String query = requestedUri.getRawQuery();
			HttpUtils.parseQuery(query, parameters);

			// send response
			File file = new File(PATH_TO_LOCAL_CFG);
			httpExchange.sendResponseHeaders(200, file.length());
			OutputStream os = httpExchange.getResponseBody();
			os.write(Files.readAllBytes(file.toPath()));
			os.close();
		}
	}

	private class EchoGetHandlerRule implements HttpHandler {
		@Override
		public void handle(HttpExchange httpExchange) throws IOException {
			Map<String, Object> parameters = new HashMap<>();
			URI requestedUri = httpExchange.getRequestURI();
			String query = requestedUri.getRawQuery();
			HttpUtils.parseQuery(query, parameters);

			// send response
			File file = new File(PATH_TO_LOCAL_RULE + name + ".js");
			httpExchange.sendResponseHeaders(200, file.length());
			OutputStream os = httpExchange.getResponseBody();
			os.write(Files.readAllBytes(file.toPath()));
			os.close();
		}
	}

	private class EchoPostHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange httpExchange) {
			/*method not used*/
		}
	}


}
