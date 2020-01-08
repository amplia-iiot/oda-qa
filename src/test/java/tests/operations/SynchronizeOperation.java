package tests.operations;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import tests.dataStructs.general.ResponseFormat;
import tests.dataStructs.write.WriteRequestStruct;
import tests.dataStructs.write.WriteResponseStruct;
import tests.discover.DiscoverData;
import tests.discover.DiscoverManager;
import tests.serializer.SerializerCBOR;
import tests.serializer.SerializerJSON;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import javax.naming.ConfigurationException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

public class SynchronizeOperation {

	private MqttClient client = new MqttClient("tcp://localhost", "ClientSync", new MemoryPersistence());
	private MqttClient EDPSimulator = new MqttClient("tcp://localhost", "EDPSync", new MemoryPersistence());
	private DiscoverManager discoverManager = new DiscoverManager("discoverManagerSyncOp");
	private static final String MESSAGE_COMMUNICATION_SUCCESS = "SUCCESSFUL";
	private final DiscoverData discoverData = new DiscoverData();

	private boolean requestIsOk;
	private boolean responseReceived;
	private boolean responseIsOk;

	private long timeIni;
	private long timeEnd;

	private String deviceId;
	private String datastreamId;

	public SynchronizeOperation() throws MqttException, IOException, ConfigurationException {
	}

	@Given("the deviceId selected to synchronize the hour: {string}")
	public void theDeviceIdSelectedToSynchronizeTheHour(String deviceId) {
		this.deviceId = deviceId;
	}

	@Given("the datastreamId selected to synchronize the hour: {string}")
	public void theDatastreamIdSelectedToSynchronizeTheHour(String datastreamId) {
		this.datastreamId = datastreamId;
	}

	@Given("A started EDP simulator to sync")
	public void aStartedEDPSimulatorToSync() throws InterruptedException, IOException, MqttException {
		EDPSimulator.connect();
		discoverManager.connect();
		EDPSimulator.setCallback(new EDPCallback());
		EDPSimulator.subscribe("oda/operation/write/request/"+this.deviceId+"/"+this.datastreamId);
		discoverManager.enable(deviceId, datastreamId, "RW");
	}

	@When("I send a request to ODA to sync the hour")
	public void iSendARequestToODAToSyncTheHour() throws MqttException, IOException, InterruptedException {
		client.connect();

		client.setCallback(new TestCallback());

		client.subscribe("odm/response/#");
		requestIsOk = false;
		responseReceived = false;
		this.timeIni = System.currentTimeMillis();
		String tempSetClock = "{\"operation\":{\"request\":{\"timestamp\":" + System.currentTimeMillis() + ",\"deviceId\":\"" + deviceId + "\",\"name\":\"SET_CLOCK_EQUIPMENT\"," +
				"\"parameters\":[],\"id\":\"4aabb9c6-61ec-43ed-b0e4-dabface44b64\"}}}";
		MqttMessage messageClock = new MqttMessage(tempSetClock.getBytes());
		client.publish("odm/request/" + discoverData.getMAINDEVICEID(), messageClock);
		String tempSyncHour = "{\"operation\":{\"request\":{\"timestamp\":1554978284595,\"deviceId\":\"" + deviceId + "\",\"name\":\"SYNCHRONIZE_CLOCK\"," +
				"\"parameters\":[],\"id\":\"4aabb9c6-61ec-43ed-b0e4-dabface44b64\"}}}";
		MqttMessage messageHour = new MqttMessage(tempSyncHour.getBytes());
		client.publish("odm/request/" + discoverData.getMAINDEVICEID(), messageHour);
	}

	@Then("I receive a response with approximately the actual hour")
	public void iReceiveAResponseWithApproximatelyTheActualHour() throws InterruptedException, MqttException {
		for(int i = 0; i < 10 && !responseReceived && !requestIsOk; i++) {
			TimeUnit.MILLISECONDS.sleep(500);
		}
		client.disconnect();
		assertTrue(responseReceived);
		assertTrue(requestIsOk);
		assertTrue(responseIsOk);
	}

	@Then("I receive a response with approximately the actual hour and disconnect simulator")
	public void iReceiveAResponseWithApproximatelyTheActualHourAndDisconnectSimulator() throws InterruptedException, MqttException {
		for(int i = 0; i < 10 && !responseReceived && !requestIsOk; i++) {
			TimeUnit.MILLISECONDS.sleep(500);
		}
		client.disconnect();
		assertTrue(responseReceived);
		assertTrue(requestIsOk);
		assertTrue(responseIsOk);
		discoverManager.disable(deviceId, datastreamId);
		EDPSimulator.disconnect();
		discoverManager.disconnect();
	}

	public class TestCallback implements MqttCallback {
		@Override
		public void connectionLost(Throwable throwable) {/* method not used*/}
		@Override
		public void messageArrived(String topic, MqttMessage message) throws IOException {
			responseReceived = true;
			ResponseFormat response = SerializerJSON.deserialize(message.getPayload(), ResponseFormat.class);
			if(response.getOperation().getResponse().getSteps().get(0).getResult().equals(MESSAGE_COMMUNICATION_SUCCESS)) {
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
			timeEnd = System.currentTimeMillis();
			topic = topic.replaceFirst("request", "response");
			WriteRequestStruct request = SerializerCBOR.deserialize(message.getPayload(), WriteRequestStruct.class);
			WriteResponseStruct response = new WriteResponseStruct(request.getId(), 200, "OK");
			if((Long) request.getValue() > timeIni && (Long) request.getValue() < timeEnd)
				requestIsOk = true;
			MqttMessage res = new MqttMessage(SerializerCBOR.serialize(response));
			EDPSimulator.publish(topic, res);
		}
		@Override
		public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {/* method not used*/}
	}
	// '{"operation":{"request":{"timestamp":1554978284595,"deviceId":"aDevice","name":"SET_CLOCK_EQUIPMENT","parameters":[],"id":"4aabb9c6-61ec-43ed-b0e4-dabface44b64"}}}'
	// '{"operation":{"request":{"timestamp":1554978284595,"deviceId":"aDevice","name":"SYNCHRONIZE_CLOCK","parameters":[],"id":"4aabb9c6-61ec-43ed-b0e4-dabface44b64"}}}'
}
