package hellocucumber.operations;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import hellocucumber.dataStructs.general.ResponseFormat;
import hellocucumber.discover.DiscoverData;
import hellocucumber.discover.DiscoverManager;
import hellocucumber.serializer.SerializerJSON;
import javafx.util.Pair;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import javax.naming.ConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DiscoverOperation {

	private MqttClient client = new MqttClient("tcp://localhost", "mqqtClientDiscoverOp", new MemoryPersistence());
	private MqttClient EDPSimulator = new MqttClient("tcp://localhost", "EDPDiscoverOp", new MemoryPersistence());
	private DiscoverManager discoverManager = new DiscoverManager("discoveryManagerDiscoverOp");
	private final DiscoverData discoverData = new DiscoverData();

	private boolean responseIsOk;
	private boolean responseReceived;
	private int count;

	public DiscoverOperation() throws MqttException, IOException, ConfigurationException {
		// Void constructor to add the Exceptions of initialize variables
	}

	@Given("A started EDP simulator to discover")
	public void aStartedEDPSimulatorToDiscover() throws MqttException {
		discoverManager.connect();
	}

	@When("^I send a request to discover to ODA$")
	public void iSendARequestToDiscoverToODA() throws MqttException {
		client.connect();
		client.setCallback(new TestCallback());
		client.subscribe("odm/response/#");
		EDPSimulator.connect();
		EDPSimulator.setCallback(new TestCallbackEDP());
		EDPSimulator.subscribe("oda/enable/#");
		responseIsOk = false;
		responseReceived = false;
		count = 0;
		String temp = "{\"operation\":{\"request\":{\"timestamp\":1557306193823,\"deviceId\":\"" + discoverData.getMAINDEVICEID() +
				"\",\"name\":\"DISCOVER\",\"parameters\":[],\"id\":\"73da9ff8-15a9-4e9a-9b2d-b6e5efbc856b\"}}}";
		client.publish("odm/request/" + discoverData.getMAINDEVICEID(), new MqttMessage(temp.getBytes()));
	}

	@Then("I have the expected datastreams")
	public void iHaveTheExpectedDatastreams() throws MqttException, InterruptedException {
		for(int i = 0; i < 10 && !responseReceived; i++) {
			TimeUnit.MILLISECONDS.sleep(500);
		}
		assertEquals(3, discoverManager.connectedQuant());
		discoverManager.disable("edp", "datStrRd");
		discoverManager.disable("edp", "datStrWr");
		discoverManager.disable("edp", "datStrRw");
		assertEquals(0, discoverManager.connectedQuant());
		discoverManager.disconnect();
	}

	@Then("I receive a successful message of discover operation")
	public void iReceiveASuccesfulMessageOfDiscoverOperation() throws InterruptedException, MqttException {
		for(int i = 0; i < 10 && !responseReceived; i++) {
			TimeUnit.MILLISECONDS.sleep(500);
		}
		client.disconnect();
		EDPSimulator.disconnect();
		assertTrue(count > 0);
		assertTrue(responseIsOk);
	}

	public class TestCallback implements MqttCallback {
		@Override
		public void connectionLost(Throwable throwable) {/* method not used*/}
		@Override
		public void messageArrived(String topic, MqttMessage message) throws IOException, MqttException {
			ResponseFormat response = SerializerJSON.deserialize(message.getPayload(), ResponseFormat.class);
			if (response.getOperation().getResponse().getSteps().get(0).getResult().equals("SUCCESSFUL"))
				responseIsOk = true;

			if (discoverManager.isconnected()) {
				ArrayList<Pair<String, String>> toEnable = new ArrayList<>();
				toEnable.add(new Pair<>("datStrRd", "RD"));
				toEnable.add(new Pair<>("datStrWr", "WR"));
				toEnable.add(new Pair<>("datStrRw", "RW"));
				discoverManager.multiEnable("edp", toEnable);
			}

			responseReceived = true;
		}
		@Override
		public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {/* method not used*/}
	}

	public class TestCallbackEDP implements MqttCallback {
		@Override
		public void connectionLost(Throwable throwable) {/* method not used*/}
		@Override
		public void messageArrived(String topic, MqttMessage message) {
			count++;
		}
		@Override
		public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {/* method not used*/}
	}
}
