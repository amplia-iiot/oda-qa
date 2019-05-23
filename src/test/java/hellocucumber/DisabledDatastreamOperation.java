package hellocucumber;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import hellocucumber.dataStructs.general.ResponseFormat;
import hellocucumber.discover.DiscoverManager;
import hellocucumber.discover.DiscoverData;
import hellocucumber.serializer.SerializerJSON;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import javax.naming.ConfigurationException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DisabledDatastreamOperation {

	private MqttClient client = new MqttClient("tcp://localhost", "mqttClientDisabledOp", new MemoryPersistence());
	private MqttClient EDPSimulator = new MqttClient("tcp://localhost", "EDPDisabledOp", new MemoryPersistence());
	private DiscoverManager discoverManager = new DiscoverManager("discoverManagerDisabledOp");
	private final DiscoverData discoverData = new DiscoverData();

	private boolean responseIsOk;
	private boolean responseReceived;
	private boolean EDPReceives;

	private String deviceId;
	private String datastreamId;

	public DisabledDatastreamOperation() throws MqttException, IOException, ConfigurationException {
		// This method is unimplemented because we need put a exception for the MqttClient
	}

	@Given("an id of device from are reading data: otherDevice")
	public void anIdOfDeviceFromAreReadingDataOtherDevice() {
		this.deviceId = "otherDevice";
	}

	@And("an id of datastream from are reading data: datastreamId")
	public void anIdOfDatastreamFromAreReadingDataDatastreamId() {
		this.datastreamId = "datastreamId";
	}

	@When("I request to read to ODA")
	public void iRequestToReadToODA() throws MqttException, IOException, InterruptedException {
		client.connect();
		EDPSimulator.connect();
		discoverManager.connect();
		client.setCallback(new TestCallback());
		EDPSimulator.setCallback(new EDPCallback());
		client.subscribe("odm/response/#");
		responseIsOk = false;
		responseReceived = false;
		EDPReceives = false;
		String temp = "{\"operation\":{\"request\":{\"timestamp\":1554978284595,\"deviceId\":\"" + deviceId + "\",\"name\":\"GET_DEVICE_PARAMETERS\"," +
				"\"parameters\":[{\"name\":\"variableList\",\"value\":{\"array\":[{\"variableName\":\"" + datastreamId +
				"\"}]}}]," + "\"id\":\"4aabb9c6-61ec-43ed-b0e4-dabface44b64\"}}}";
		MqttMessage message = new MqttMessage(temp.getBytes());
		discoverManager.enable(deviceId, datastreamId, "RD");
		discoverManager.disable(deviceId, datastreamId);
		client.publish("odm/request/" + discoverData.getMAINDEVICEID(), message);
	}

	@Then("ODA shouldn't send anything")
	public void odaShouldnTSendAnything() throws InterruptedException, MqttException {
		for(int i = 0; i < 10 && !responseReceived; i++) {
			TimeUnit.MILLISECONDS.sleep(500);
		}
		discoverManager.disable(deviceId, datastreamId);
		client.disconnect();
		EDPSimulator.disconnect();
		discoverManager.disconnect();
		assertTrue(responseIsOk);
		assertFalse(EDPReceives);
	}

	public class TestCallback implements MqttCallback {
		@Override
		public void connectionLost(Throwable throwable) {/* method not used*/}
		@Override
		public void messageArrived(String topic, MqttMessage message) throws IOException{
			responseReceived = true;
			ResponseFormat response = SerializerJSON.deserialize(message.getPayload(), ResponseFormat.class);
			if(response.getOperation().getResponse().getSteps().get(0).getResponse().get(0).getResultDescription().equals("No datastream found")) {
				responseIsOk = true;
			}
		}
		@Override
		public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {/* method not used*/}
	}

	public class EDPCallback implements MqttCallback {
		@Override
		public void connectionLost(Throwable throwable) {/* method not used*/}
		@Override
		public void messageArrived(String topic, MqttMessage message) {
			EDPReceives = true;
		}
		@Override
		public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {/* method not used*/}
	}
}
