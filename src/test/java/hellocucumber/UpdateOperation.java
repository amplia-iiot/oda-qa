package hellocucumber;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import hellocucumber.dataStructs.update.UpdateResponseStruct;
import org.eclipse.paho.client.mqttv3.*;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

public class UpdateOperation {

	private MqttClient client = new MqttClient("tcp://localhost", "ClientSet");
	private static final String MESSAGE_COMMUNICATION_SUCCESS = "SUCCESSFUL";
	private static final int PORT_HTTP_SERVER = 9000;
	private HttpServer server;
	private static final String PATH_TO_LOCAL_CFG = "./src/test/resources/hellocucumber/es.amplia.oda.datastreams.mqtt.cfg";

	private boolean responseReceived;
	private boolean responseIsOk;

	public UpdateOperation() throws MqttException {
		// This method is unimplemented because we need put a exception for the MqttClient
	}

	@When("I send a request to ODA to change the configuration")
	public void iSendARequestToODAToChangeTheConfiguration() throws MqttException, IOException {
		this.client.connect();


		this.client.setCallback(new TestCallback());
		this.client.subscribe("odm/response/#");

		this.responseIsOk = false;
		this.responseReceived = false;

		server = HttpServer.create(new InetSocketAddress(PORT_HTTP_SERVER), 0);
		server.createContext("/", new RootHandler());
		server.createContext("/echoHeader", new EchoHeaderHandler());
		server.createContext("/echoGet", new EchoGetHandler());
		server.createContext("/echoPost", new EchoPostHandler());
		server.setExecutor(null);
		server.start();

		String temp = "{\"operation\":{\"request\":{\"timestamp\":1557395219834,\"name\":\"UPDATE\",\"parameters\":[{" +
				"\"name\":\"bundleName\",\"value\":{\"string\":\"oda-smart-energy-test\"}},{\"name\":\"bundleVersion\"," +
				"\"value\":{\"string\":\"2.0.0\"}},{\"name\":\"deploymentElements\",\"type\":\"string\",\"value\":{" +
				"\"array\":[{\"name\":\"es.amplia.oda.datastreams.mqtt\",\"version\":\"2.0.0\",\"type\":\"CONFIGURATION" +
				"\",\"downloadUrl\":\"" +
				//"http://api.opengate.es/south/bundles/deploymentElement/deviceOda/18ae3bdd-8a0b-4a68-9049-03d2a191fcaf/27766837/es.amplia.oda.datastreams.mqtt_2.0.0.cfg" +
				"http://localhost:" + PORT_HTTP_SERVER + "/echoGet" +
				"\",\"path\":\"configuration\",\"order\":1,\"operation\":\"UPGRADE\",\"validators\":[],\"size\":334,\"" +
				"oldVersion\":\"1.0.0\",\"oldName\":\"es.amplia.oda.datastreams.mqtt\",\"oldPath\":\"configuration\"" +
				"}]}}],\"id\":\"48589c6e-3d9f-4e59-a066-81f357fb6cf8\"}}}";
		client.publish("odm/request/" + OdaLocation.MAINDEVICEID, new MqttMessage(temp.getBytes()));
	}

	@Then("the new configuration is the same that the file")
	public void theNewConfigurationIsTheSameThatTheFile() throws InterruptedException, MqttException {
		for(int i = 0; i < 10 && !responseReceived; i++) {
			TimeUnit.MILLISECONDS.sleep(500);
		}
		server.stop(0);
		client.disconnect();
		assertTrue(responseIsOk);
	}

	public class TestCallback implements MqttCallback {
		@Override
		public void connectionLost(Throwable throwable) {/* method not used*/}
		@Override
		public void messageArrived(String topic, MqttMessage message) throws IOException {
			responseReceived = true;
			UpdateResponseStruct response = SerializerJSON.deserialize(message.getPayload(), UpdateResponseStruct.class);
			if(response.getResultResponse().equals(MESSAGE_COMMUNICATION_SUCCESS)) {
				responseIsOk = true;
			}
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

	private class EchoGetHandler implements HttpHandler {
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

	private class EchoPostHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange httpExchange) {
			/*method not used*/
		}
	}
}
