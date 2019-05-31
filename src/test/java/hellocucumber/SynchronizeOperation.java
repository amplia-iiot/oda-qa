package hellocucumber;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import hellocucumber.dataStructs.general.ResponseFormat;
import hellocucumber.dataStructs.write.WriteRequestStruct;
import hellocucumber.dataStructs.write.WriteResponseStruct;
import hellocucumber.discover.DiscoverData;
import hellocucumber.discover.DiscoverManager;
import hellocucumber.serializer.SerializerCBOR;
import hellocucumber.serializer.SerializerJSON;
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

	@Given("the deviceId selected to synchronize the hour: syncDevice")
	public void theDeviceIdSelectedToSynchronizeTheHour() {
		this.deviceId = "syncDevice";
	}

	@Given("the datastreamId selected to synchronize the hour: syncDatastream")
	public void theDatastreamIdSelectedToSynchronizeTheHour() {
		this.datastreamId = "syncHour";
	}

	@When("I send a request to ODA to sync the hour")
	public void iSendARequestToODAToSyncTheHour() throws MqttException, IOException, InterruptedException {
		client.connect();
		EDPSimulator.connect();
		discoverManager.connect();
		client.setCallback(new TestCallback());
		EDPSimulator.setCallback(new EDPCallback());
		client.subscribe("odm/response/#");
		EDPSimulator.subscribe("oda/operation/write/request/"+this.deviceId+"/"+this.datastreamId);
		requestIsOk = false;
		responseReceived = false;
		this.timeIni = System.currentTimeMillis();
		discoverManager.enable(deviceId, datastreamId, "WR");
		String temp = "{\"operation\":{\"request\":{\"timestamp\":1554978284595,\"deviceId\":\"" + deviceId + "\",\"name\":\"SYNC_HOUR\"," +
				"\"parameters\":[],\"id\":\"4aabb9c6-61ec-43ed-b0e4-dabface44b64\"}}}";
		MqttMessage message = new MqttMessage(temp.getBytes());
		client.publish("odm/request/" + discoverData.getMAINDEVICEID(), message);
	}

	@Then("I receive a response with approximately the actual hour")
	public void iReceiveAResponseWithApproximatelyTheActualHour() throws InterruptedException, MqttException {
		for(int i = 0; i < 10 && !responseReceived; i++) {
			TimeUnit.MILLISECONDS.sleep(500);
		}
		discoverManager.disable(deviceId, datastreamId);
		client.disconnect();
		EDPSimulator.disconnect();
		discoverManager.disconnect();
		assertTrue(responseReceived);
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
			WriteResponseStruct response = new WriteResponseStruct(request.getId(), 201, "OK");
			if((Long) request.getValue() > timeIni && (Long) request.getValue() < timeEnd)
				requestIsOk = true;
			MqttMessage res = new MqttMessage(SerializerCBOR.serialize(response));
			EDPSimulator.publish(topic, res);
		}
		@Override
		public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {/* method not used*/}
	}
}
