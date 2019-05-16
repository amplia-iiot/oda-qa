package hellocucumber;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import hellocucumber.dataStructs.general.ResponseFormat;
import hellocucumber.dataStructs.read.ReadRequestStruct;
import hellocucumber.dataStructs.read.ReadResponseStruct;
import hellocucumber.discover.DiscoverManager;
import hellocucumber.http.OdaLocation;
import hellocucumber.serializer.SerializerCBOR;
import hellocucumber.serializer.SerializerJSON;
import org.eclipse.paho.client.mqttv3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

public class RefreshInfoOperation {

	private MqttClient client = new MqttClient("tcp://localhost", "refreshClient");
	private MqttClient EDPSimulator = new MqttClient("tcp://localhost", "refreshEDP");

	private boolean responseIsOk;
	private boolean responseReceived;

	private String deviceId;
	private String datastreamId;
	private String responses;

	public RefreshInfoOperation() throws MqttException {
	}

	@Given("id of target device to refresh: anotherDevice")
	public void idOfTargetDeviceToRefreshAnotherDevice() {
		deviceId = "anotherDevice";
		datastreamId = "testing";
		responses = "testIsOk";
	}

	@When("I send a request to ODA to refresh the data")
	public void iSendARequestToODAToRefreshTheData() throws MqttException, IOException {
		client.connect();
		EDPSimulator.connect();
		DiscoverManager.connect();
		client.setCallback(new TestCallback());
		EDPSimulator.setCallback(new EDPCallback());
		client.subscribe("odm/response/#");
		EDPSimulator.subscribe("oda/operation/read/request/#");
		responseIsOk = false;
		responseReceived = false;
		DiscoverManager.enable(deviceId, datastreamId, "RD");
		String temp = "{\"operation\":{\"request\":{\"timestamp\":1557306193823,\"deviceId\":\"" + deviceId + "\",\"" +
				"name\":\"REFRESH_INFO\",\"parameters\":[],\"id\":\"73da9ff8-15a9-4e9a-9b2d-b6e5efbc856b\"}}}";
		client.publish("odm/request/" + OdaLocation.MAINDEVICEID, new MqttMessage(temp.getBytes()));
	}

	@Then("I receive a response of all datastreams and data send to ODA is the same that received by EDP")
	public void iReceiveAResponseOfAllDatastreamsAndDataSendToODAIsTheSameThatReceivedByEDP() throws InterruptedException, MqttException {
		for(int i = 0; i < 10 && !responseReceived; i++) {
			TimeUnit.MILLISECONDS.sleep(500);
		}
		DiscoverManager.disable(deviceId, datastreamId);
		client.disconnect();
		EDPSimulator.disconnect();
		DiscoverManager.disconnect();
		assertTrue(responseIsOk);
	}

	public class TestCallback implements MqttCallback {
		@Override
		public void connectionLost(Throwable throwable) {/* method not used*/}
		@Override
		public void messageArrived(String topic, MqttMessage message) throws IOException {
			responseReceived = true;
			ResponseFormat response = SerializerJSON.deserialize(message.getPayload(), ResponseFormat.class);
			if(responses.equals(response.getOperation().getResponse().getSteps().get(0).getResponse().get(0).getVariableValue())) {
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
			ReadResponseStruct response = new ReadResponseStruct(request.getId(), 200, "OK", System.currentTimeMillis(), responses);
			MqttMessage temp = new MqttMessage(SerializerCBOR.serialize(response));
			EDPSimulator.publish(topic, temp);
		}
		@Override
		public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {/* method not used*/}
	}
}
