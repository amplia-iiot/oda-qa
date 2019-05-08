package hellocucumber;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import hellocucumber.dataStructs.general.ResponseFormat;
import hellocucumber.dataStructs.write.WriteRequestStruct;
import hellocucumber.dataStructs.write.WriteResponseStruct;
import org.eclipse.paho.client.mqttv3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

public class SetOperation {


	private static final String MAINDEVICEID = "testDevice";
	private MqttClient client = new MqttClient("tcp://localhost", "ClientSet");
	private MqttClient EDPSimulator = new MqttClient("tcp://localhost", "EDPSet");
	private static final com.fasterxml.jackson.databind.ObjectMapper MAPPER = new ObjectMapper(new CBORFactory());
	private static final ObjectMapper MAPPERJSON = new ObjectMapper();
	private static final String MESSAGE_COMMUNICATION_SUCCESS = "SUCCESS";

	private boolean requestIsOk;
	private boolean responseReceived;
	private boolean responseIsOk;
	private int value;

	private String deviceId;
	private String datastreamId;

	public SetOperation() throws MqttException {
		// This method is unimplemented because we need put a exception for the MqttClient
	}

	@Given("new value for the datastream: 22")
	public void newValueForTheDatastreamValue() {
		this.value = 22;
	}

	@And("id of target device to write: otherDevice")
	public void idOfTargetDeviceToWriteOtherDevice() {
		this.deviceId = "otherDevice";
	}

	@And("id of target datastream to write: testDatastream")
	public void idOfTargetDatastreamToWriteTestDatastream() {
		this.datastreamId = "testDatastream";
	}

	/*@Given("new value for the datastream: {string}")
	public void newValueForTheDatastream(String value) {
		this.value = Integer.parseInt(value);
	}

	@And("id of target device to write: {string}")
	public void idOfTargetDeviceToWrite(String deviceId) {
		this.deviceId = deviceId;
	}

	@And("id of target datastream to write: {string}")
	public void idOfTargetDatastreamToWrite(String datastreamId) {
		this.datastreamId = datastreamId;
	}*/

	@When("I send a request to ODA to set the data")
	public void iSendARequestToODAToSetTheData() throws MqttException, IOException {
		client.connect();
		EDPSimulator.connect();
		DiscoverManager.connect();
		client.setCallback(new TestCallback());
		EDPSimulator.setCallback(new EDPCallback());
		client.subscribe("odm/response/#");
		EDPSimulator.subscribe("oda/operation/write/request/#");
		requestIsOk = false;
		responseReceived = false;
		DiscoverManager.enable(deviceId, datastreamId, "WR");
		String temp = "{\"operation\":{\"request\":{\"timestamp\":1554978284595,\"deviceId\":\"" + deviceId + "\",\"name\":\"SET_DEVICE_PARAMETERS\"," +
				"\"parameters\":[{\"name\":\"variableList\",\"value\":{\"array\":[{\"variableName\":\"" + datastreamId +
				"\",\"variableValue\":" + value + "}]}}],\"id\":\"4aabb9c6-61ec-43ed-b0e4-dabface44b64\"}}}";
		MqttMessage message = new MqttMessage(temp.getBytes());
		client.publish("odm/request/" + MAINDEVICEID, message);
	}

	@Then("I receive a response and data send to ODA is the same that received by EDP")
	public void iReceiveAResponseAndDataSendToODAIsTheSameThatReceivedByEDP() throws InterruptedException, MqttException {
		for(int i = 0; i < 10 && !responseReceived; i++) {
			TimeUnit.MILLISECONDS.sleep(500);
		}
		DiscoverManager.disable(deviceId, datastreamId);
		client.disconnect();
		EDPSimulator.disconnect();
		DiscoverManager.disconnect();
		assertTrue(requestIsOk);
		assertTrue(responseIsOk);
	}

	public class TestCallback implements MqttCallback {
		@Override
		public void connectionLost(Throwable throwable) {/* method not used*/}
		@Override
		public void messageArrived(String topic, MqttMessage message) throws IOException {
			responseReceived = true;
			ResponseFormat response = deserializeJSON(message.getPayload(), ResponseFormat.class);
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
			WriteRequestStruct request = deserialize(message.getPayload(), WriteRequestStruct.class);
			WriteResponseStruct response = new WriteResponseStruct(request.getId(), 201, "OK");
			if(value == ((Double) request.getValue()).intValue())
				requestIsOk = true;
			MqttMessage res = new MqttMessage(serialize(response));
			EDPSimulator.publish(topic, res);
		}
		@Override
		public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {/* method not used*/}
	}

	private <T> T deserialize(byte[] value, Class<T> type) throws IOException {
		return MAPPER.readValue(value, type);
	}

	private byte[] serialize(Object value) throws IOException {
		return MAPPER.writeValueAsBytes(value);
	}

	private <T> T deserializeJSON(byte[] value, Class<T> type) throws IOException {
		return MAPPERJSON.readValue(value, type);
	}
}
