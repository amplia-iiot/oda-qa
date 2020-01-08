package tests.operations;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import tests.dataStructs.general.ResponseFormat;
import tests.dataStructs.read.ReadRequestStruct;
import tests.dataStructs.read.ReadResponseStruct;
import tests.discover.DiscoverData;
import tests.serializer.SerializerCBOR;
import tests.discover.DiscoverManager;
import tests.serializer.SerializerJSON;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import javax.naming.ConfigurationException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

public class RefreshInfoOperation {

	private MqttClient client = new MqttClient("tcp://localhost", "refreshClient", new MemoryPersistence());
	private MqttClient EDPSimulator = new MqttClient("tcp://localhost", "refreshEDP", new MemoryPersistence());
	private DiscoverManager discoverManager = new DiscoverManager("discoverManagerRefreshOp");
	private final DiscoverData discoverData = new DiscoverData();

	private boolean responseIsOk;
	private boolean responseReceived;

	private String deviceId;
	private String datastreamId;
	private String responses;

	public RefreshInfoOperation() throws MqttException, IOException, ConfigurationException {
	}

	@Given("id of target device to refresh: {string}")
	public void idOfTargetDeviceToRefresh(String deviceId) {
		this.deviceId = deviceId;
	}

	@Given("id of target datastream to refresh: {string}")
	public void idOfTargetDatastreamToRefresh(String datastreamId) {
		this.datastreamId = datastreamId;
	}

	@Given("data that we will use to test: {string}")
	public void dataThatWeWillUseToTest(String responses) {
		this.responses = responses;
	}

	@Given("A started EDP simulator to refresh")
	public void aStartedEDPSimulatorToRefresh() throws MqttException, IOException, InterruptedException {
		EDPSimulator.connect();
		discoverManager.connect();
		EDPSimulator.setCallback(new EDPCallback());
		EDPSimulator.subscribe("oda/operation/read/request/#");
		discoverManager.enable(deviceId, datastreamId, "RD");
	}

	@When("I send a request to ODA to refresh the data")
	public void iSendARequestToODAToRefreshTheData() throws MqttException {
		client.connect();
		client.setCallback(new TestCallback());
		client.subscribe("odm/response/#");
		responseIsOk = false;
		responseReceived = false;
		String temp = "{\"operation\":{\"request\":{\"timestamp\":1557306194823,\"deviceId\":\"" + deviceId + "\",\"" +
				"name\":\"REFRESH_INFO\",\"parameters\":[],\"id\":\"73da9ff8-15a9-4e9a-9b2d-b6e5efbc856b\"}}}";
		client.publish("odm/request/" + discoverData.getMAINDEVICEID(), new MqttMessage(temp.getBytes()));
	}

	@Then("I receive a response of all datastreams")
	public void iReceiveAResponseOfAllDatastreams() throws InterruptedException, MqttException {
		for(int i = 0; i < 20 && !responseReceived; i++) {
			TimeUnit.MILLISECONDS.sleep(500);
		}
		client.disconnect();
		assertTrue(responseIsOk);
	}

	@Then("Data send to ODA is the same that received by EDP")
	public void dataSendToODAIsTheSameThatReceivedByEDP() throws InterruptedException, MqttException {
		for(int i = 0; i < 10 && !responseReceived; i++) {
			TimeUnit.MILLISECONDS.sleep(500);
		}
		discoverManager.disable(deviceId, datastreamId);
		EDPSimulator.disconnect();
		discoverManager.disconnect();
		assertTrue(responseIsOk);
	}

	public class TestCallback implements MqttCallback {
		@Override
		public void connectionLost(Throwable throwable) {/* method not used*/}

		@Override
		public void messageArrived(String topic, MqttMessage message) throws IOException {
			ResponseFormat response = SerializerJSON.deserialize(message.getPayload(), ResponseFormat.class);
			if(discoverManager.isconnected()) {
				if (responses.equals(response.responseValue())) {
					responseIsOk = true;
				}
			}
			else {
				if (response.getOperation().getResponse().getSteps().get(0).getResponse().size() == 23) {
					responseIsOk = true;
				}
			}
			responseReceived = true;
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
	/*    '{"operation":{"request":{"timestamp":1571993751183,"deviceId":"Tm1234","name":"REFRESH_INFO","parameters":[],"id":"b949fabf-4734-46fe-937c-ea1e51fc2cf7"}}}' */
}
