package es.amplia.oda.operation.set;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import utils.dataStructs.ResponseFormat;
import es.amplia.oda.operation.set.dataStructs.WriteRequestStruct;
import es.amplia.oda.operation.set.dataStructs.WriteResponseStruct;
import utils.discover.DiscoverManager;
import utils.discover.DiscoverData;
import utils.serializer.SerializerCBOR;
import utils.serializer.SerializerJSON;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import javax.naming.ConfigurationException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

public class SetOperation {

	private MqttClient client = new MqttClient("tcp://localhost", "ClientSet", new MemoryPersistence());
	private MqttClient EDPSimulator = new MqttClient("tcp://localhost", "EDPSet", new MemoryPersistence());
	private DiscoverManager discoverManager = new DiscoverManager("discoverManagerSetOp");
	private static final String MESSAGE_COMMUNICATION_SUCCESS = "SUCCESS";
	private final DiscoverData discoverData = new DiscoverData();

	private boolean requestIsOk;
	private boolean responseReceived;
	private boolean responseIsOk;
	private int value;

	private String deviceId;
	private String datastreamId;

	public SetOperation() throws MqttException, IOException, ConfigurationException {
		// This method is unimplemented because we need put a exception for the MqttClient
	}

	@Given("new value for the datastream: {string}")
	public void newValueForTheDatastreamValue(String value) {
		this.value = Integer.parseInt(value);
	}

	@Given("id of target device to write: {string}")
	public void idOfTargetDeviceToWriteOtherDevice(String deviceId) {
		this.deviceId = deviceId;
	}

	@Given("id of target datastream to write: {string}")
	public void idOfTargetDatastreamToWriteTestDatastream(String datastreamId) {
		this.datastreamId = datastreamId;
	}

	@Given("A started EDP simulator to set")
	public void aStartedEDPSimulatorToSet() throws MqttException, IOException, InterruptedException {
		EDPSimulator.connect();
		discoverManager.connect();
		EDPSimulator.setCallback(new EDPCallback());
		EDPSimulator.subscribe("oda/operation/write/request/#");
		discoverManager.enable(deviceId, datastreamId, "WR");
	}

	@When("I send a request to ODA to set the data")
	public void iSendARequestToODAToSetTheData() throws MqttException {
		client.connect();
		client.setCallback(new TestCallback());
		client.subscribe("odm/response/#");
		requestIsOk = false;
		responseReceived = false;
		String temp = "{\"operation\":{\"request\":{\"timestamp\":1554978284595,\"deviceId\":\"" + deviceId + "\",\"name\":\"SET_DEVICE_PARAMETERS\"," +
				"\"parameters\":[{\"name\":\"variableList\",\"value\":{\"array\":[{\"variableName\":\"" + datastreamId +
				"\",\"variableValue\":" + value + "}]}}],\"id\":\"4aabb9c6-61ec-43ed-b0e4-dabface44b64\"}}}";
		MqttMessage message = new MqttMessage(temp.getBytes());
		client.publish("odm/request/" + discoverData.getMAINDEVICEID(), message);
	}

	@Then("data send to ODA is the same that received by EDP")
	public void dataSendToODAIsTheSameThatReceivedByEDP() throws MqttException, InterruptedException {
		for(int i = 0; i < 6 && !responseReceived; i++) {
			TimeUnit.MILLISECONDS.sleep(500);
		}
		discoverManager.disable(deviceId, datastreamId);
		EDPSimulator.disconnect();
		discoverManager.disconnect();
		assertTrue(requestIsOk);
	}

	@Then("I receive a response")
	public void iReceiveAResponse() throws MqttException, InterruptedException {
		for(int i = 0; i < 10 && !responseReceived; i++) {
			TimeUnit.MILLISECONDS.sleep(500);
		}
		client.disconnect();
		assertTrue(responseIsOk);
	}

	public class TestCallback implements MqttCallback {
		@Override
		public void connectionLost(Throwable throwable) {/* method not used*/}
		@Override
		public void messageArrived(String topic, MqttMessage message) throws IOException {
			responseReceived = true;
			ResponseFormat response = SerializerJSON.deserialize(message.getPayload(), ResponseFormat.class);
			if(response.verifyStepResult(MESSAGE_COMMUNICATION_SUCCESS)) {
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
		public void messageArrived(String topic, MqttMessage message) throws IOException, MqttException {
			topic = topic.replaceFirst("request", "response");
			WriteRequestStruct request = SerializerCBOR.deserialize(message.getPayload(), WriteRequestStruct.class);
			WriteResponseStruct response = new WriteResponseStruct(request.getId(), 200, "OK");
			if(value == ((int) request.getValue()))
				requestIsOk = true;
			MqttMessage res = new MqttMessage(SerializerCBOR.serialize(response));
			EDPSimulator.publish(topic, res);
		}
		@Override
		public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {/* method not used*/}
	}

	// '{"operation":{"request":{"timestamp":1554978284595,"deviceId":"Tm1234","name":"SET_DEVICE_PARAMETERS","parameters":[{"name":"variableList","value":{"array":[{"variableName":"q","variableValue":17}]}}],"id":"4aabb9c6-61ec-43ed-b0e4-dabface44b64"}}}'
}
