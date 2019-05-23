package hellocucumber;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import hellocucumber.dataStructs.general.ResponseFormat;
import hellocucumber.dataStructs.read.ReadRequestStruct;
import hellocucumber.dataStructs.read.ReadResponseStruct;
import hellocucumber.discover.DiscoverManager;
import hellocucumber.discover.DiscoverData;
import hellocucumber.serializer.SerializerCBOR;
import hellocucumber.serializer.SerializerJSON;
import org.eclipse.paho.client.mqttv3.*;

import javax.naming.ConfigurationException;
import java.io.*;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

public class GetOperation {

	private MqttClient client = new MqttClient("tcp://localhost", "mqqtClientGetOp");
	private MqttClient EDPSimulator = new MqttClient("tcp://localhost", "EDPGetOp");
	private DiscoverManager discoverManager = new DiscoverManager("discoverManagerGetOp");
	private final DiscoverData discoverData = new DiscoverData();

	private boolean responseIsOk;
	private boolean responseReceived;
	private int value;

	private String deviceId;
	private String datastreamId;

	public GetOperation() throws MqttException, IOException, ConfigurationException {
		// This method is unimplemented because we need put a exception for the MqttClient
	}

	@Given("id of target device: {string}")
	public void idOfTargetDevice(String deviceId) {
		this.deviceId = deviceId;
	}

	@And("id of target datastream: {string}")
	public void idOfTargetDatastream(String datastreamId) {
		this.datastreamId = datastreamId;
	}

	@When("I send a request to ODA with required data")
	public void iSendARequestToODAWithRequiredData() throws MqttException, IOException {
		this.client.connect();
		this.EDPSimulator.connect();
		discoverManager.connect();

		this.client.setCallback(new TestCallback());
		this.EDPSimulator.setCallback(new EDPCallback());

		this.client.subscribe("odm/response/#");
		this.EDPSimulator.subscribe("oda/operation/read/request/#");

		this.responseIsOk = false;
		this.responseReceived = false;
		this.value = 33;

		discoverManager.enable(deviceId, datastreamId, "RD");
		String temp = "{\"operation\":{\"request\":{\"timestamp\":1554978284595,\"deviceId\":\"" + deviceId + "\",\"name\":\"GET_DEVICE_PARAMETERS\"," +
				"\"parameters\":[{\"name\":\"variableList\",\"value\":{\"array\":[{\"variableName\":\"" + datastreamId +
				"\"}]}}]," + "\"id\":\"4aabb9c6-61ec-43ed-b0e4-dabface44b64\"}}}";
		client.publish("odm/request/" + discoverData.getMAINDEVICEID(), new MqttMessage(temp.getBytes()));
	}

	/*
	"{"operation":{"request":{"timestamp":1554978284595,"deviceId":"deviceId","name":"GET_DEVICE_PARAMETERS","parameters":[{"name":"variableList","value":{"array":[{"variableName":"datastreamId"}]}}],"id":"4aabb9c6-61ec-43ed-b0e4-dabface44b64"}}}";
	 */

	@Then("I receive the same data that EPC Simulator send to ODA")
	public void iReceiveTheSameDataThatEPCSimulatorSendToODA() throws InterruptedException, MqttException {
		for(int i = 0; i < 10 && !responseReceived; i++) {
			TimeUnit.MILLISECONDS.sleep(500);
		}
		discoverManager.disable(deviceId, datastreamId);
		client.disconnect();
		EDPSimulator.disconnect();
		discoverManager.disconnect();
		assertTrue(responseIsOk);
	}

	public class TestCallback implements MqttCallback {
		@Override
		public void connectionLost(Throwable throwable) {/* method not used*/}
		@Override
		public void messageArrived(String topic, MqttMessage message) throws IOException{
			responseReceived = true;
			ResponseFormat response = SerializerJSON.deserialize(message.getPayload(), ResponseFormat.class);
			if(value == (Integer) response.getOperation().getResponse().getSteps().get(0).getResponse().get(0).getVariableValue()) {
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
			ReadRequestStruct request = SerializerCBOR.deserialize(message.getPayload(), ReadRequestStruct.class);
			ReadResponseStruct response = new ReadResponseStruct(request.getId(), 200, "OK", System.currentTimeMillis(), value);
			EDPSimulator.publish(topic, new MqttMessage(SerializerCBOR.serialize(response)));
		}
		@Override
		public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {/* method not used*/}
	}
}
