package hellocucumber;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import hellocucumber.dataStructs.general.ResponseFormat;
import hellocucumber.dataStructs.write.WriteRequestStruct;
import hellocucumber.dataStructs.write.WriteResponseStruct;
import hellocucumber.discover.DiscoverManager;
import hellocucumber.discover.DiscoverData;
import hellocucumber.serializer.SerializerCBOR;
import hellocucumber.serializer.SerializerJSON;
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

	@Given("^new value for the datastream: 22")
	public void newValueForTheDatastreamValue() {
		this.value = 22;
	}

	@And("id of target device to write: counter")
	public void idOfTargetDeviceToWriteOtherDevice() {
		this.deviceId = "counter";
	}

	@And("id of target datastream to write: visitors")
	public void idOfTargetDatastreamToWriteTestDatastream() {
		this.datastreamId = "visitors";
	}

	@When("I send a request to ODA to set the data")
	public void iSendARequestToODAToSetTheData() throws MqttException, IOException, InterruptedException {
		client.connect();
		EDPSimulator.connect();
		discoverManager.connect();
		client.setCallback(new TestCallback());
		EDPSimulator.setCallback(new EDPCallback());
		client.subscribe("odm/response/#");
		EDPSimulator.subscribe("oda/operation/write/request/#");
		requestIsOk = false;
		responseReceived = false;
		discoverManager.enable(deviceId, datastreamId, "WR");
		String temp = "{\"operation\":{\"request\":{\"timestamp\":1554978284595,\"deviceId\":\"" + deviceId + "\",\"name\":\"SET_DEVICE_PARAMETERS\"," +
				"\"parameters\":[{\"name\":\"variableList\",\"value\":{\"array\":[{\"variableName\":\"" + datastreamId +
				"\",\"variableValue\":" + value + "}]}}],\"id\":\"4aabb9c6-61ec-43ed-b0e4-dabface44b64\"}}}";
		MqttMessage message = new MqttMessage(temp.getBytes());
		client.publish("odm/request/" + discoverData.getMAINDEVICEID(), message);
	}

	@Then("I receive a response and data send to ODA is the same that received by EDP")
	public void iReceiveAResponseAndDataSendToODAIsTheSameThatReceivedByEDP() throws InterruptedException, MqttException {
		for(int i = 0; i < 10 && !responseReceived; i++) {
			TimeUnit.MILLISECONDS.sleep(500);
		}
		discoverManager.disable(deviceId, datastreamId);
		client.disconnect();
		EDPSimulator.disconnect();
		discoverManager.disconnect();
		assertTrue(requestIsOk);
		assertTrue(responseIsOk);
	}

	public class TestCallback implements MqttCallback {
		@Override
		public void connectionLost(Throwable throwable) {/* method not used*/}
		@Override
		public void messageArrived(String topic, MqttMessage message) throws IOException {
			responseReceived = true;
			ResponseFormat response = SerializerJSON.deserialize(message.getPayload(), ResponseFormat.class);
			if(response.getOperation().getResponse().getSteps().get(0).getResponse().get(0).getResultCode().equals(MESSAGE_COMMUNICATION_SUCCESS)) {
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
			WriteResponseStruct response = new WriteResponseStruct(request.getId(), 201, "OK");
			if(value == ((Double) request.getValue()).intValue())
				requestIsOk = true;
			MqttMessage res = new MqttMessage(SerializerCBOR.serialize(response));
			EDPSimulator.publish(topic, res);
		}
		@Override
		public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {/* method not used*/}
	}
}
