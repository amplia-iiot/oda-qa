package hellocucumber;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import hellocucumber.dataStructs.event.EventDatapoint;
import hellocucumber.dataStructs.event.EventMessage;
import hellocucumber.dataStructs.event.EventResponseStruct;
import hellocucumber.dataStructs.event.MessageDatastreams;
import hellocucumber.discover.DiscoverManager;
import hellocucumber.serializer.SerializerCBOR;
import hellocucumber.serializer.SerializerJSON;
import javafx.util.Pair;
import org.eclipse.paho.client.mqttv3.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertFalse;

public class EventOperation {

	private MqttClient client = new MqttClient("tcp://localhost", "mqqtClientEventOp");
	private MqttClient EDPSimulator = new MqttClient("tcp://localhost", "EDPEventOp");
	private DiscoverManager discoverManager = new DiscoverManager("discoveryManagerEventOp");

	private ArrayList<Boolean> responseIsOk = new ArrayList<>();
	private ArrayList<Boolean> responseReceived = new ArrayList<>();

	private ArrayList<String> values = new ArrayList<>();
	private String deviceId;
	private ArrayList<String> datastreamIds = new ArrayList<>();

	public EventOperation() throws MqttException {
		// This method is unimplemented because we need put a exception for the MqttClient
	}

	@Given("^a value to send: today it is raining$")
	public void aValueToSend() {
		values.add("today it is raining");
	}

	@And("an id of device what are sending data: otherDevice")
	public void anIdOfDeviceWhatAreSendingDataTestDevice() {
		deviceId = "otherDevice";
	}

	@And("an id of datastream from are sending data: datastreamId")
	public void anIdOfDatastreamFromAreSendingDataDatastreamId() {
		datastreamIds.add("datastreamId");
	}

	@When("I send a event to ODA")
	public void iSendAEventToODA() throws MqttException, IOException {
		client.connect();
		EDPSimulator.connect();
		discoverManager.connect();
		client.setCallback(new TestCallback());
		client.subscribe("odm/iot/#");
		responseIsOk.add(false);
		responseReceived.add( false);
		EventDatapoint dp = new EventDatapoint(System.currentTimeMillis(), values.get(0));
		MqttMessage message = new MqttMessage(SerializerCBOR.serialize(dp));
		discoverManager.enable(deviceId, datastreamIds.get(0), "RW");
		EDPSimulator.publish("oda/event/" + deviceId + "/" + datastreamIds.get(0), message);
	}

	@Then("ODA receive data and send it")
	public void odaReceiveDataAndSendItToOdmIot() throws InterruptedException, MqttException, IOException {
		for(int i = 0; i < 10 && responseReceived.contains(Boolean.FALSE); i++) {
			TimeUnit.MILLISECONDS.sleep(500);
		}
		discoverManager.multiDisable(deviceId, datastreamIds);
		client.disconnect();
		EDPSimulator.disconnect();
		discoverManager.disconnect();
		assertFalse(responseIsOk.contains(Boolean.FALSE));
	}

	@Given("various values to send: raining, sunny and cloudy")
	public void variousValuesToSendRainingSunnyAndCloudy() {
		values.add("raining");
		values.add("sunny");
		values.add("cloudy");
	}

	@And("respective id of datastreams from are sending data: weatherNorth, weatherSouth, weatherWest")
	public void respectiveIdOfDatastreamsFromAreSendingDataWeatherNorthWeatherSouthWeatherWest() {
		datastreamIds.add("weatherNorth");
		datastreamIds.add("weatherSouth");
		datastreamIds.add("weatherWest");
	}

	@When("I send various events to ODA")
	public void iSendVariousEventsToODA() throws MqttException, IOException {
		client.connect();
		EDPSimulator.connect();
		discoverManager.connect();
		client.setCallback(new TestCallback());
		client.subscribe("odm/iot/#");
		ArrayList<Pair<String,String>> enablingDatastreams = new ArrayList<>();
		EventMessage eventMessage = new EventMessage();
		for (int i = 0; i < values.size(); i++) {
			Pair<String,String> pair = new Pair<>(datastreamIds.get(i), "RW");
			enablingDatastreams.add(pair);
			responseIsOk.add(false);
			responseReceived.add(false);
			MessageDatastreams messageDatastreams = new MessageDatastreams();
			messageDatastreams.setDatastreamId(datastreamIds.get(i));
			messageDatastreams.setAt(System.currentTimeMillis());
			messageDatastreams.setValue(values.get(i));
			eventMessage.addDatastream(messageDatastreams);
		}
		discoverManager.multiEnable(deviceId, enablingDatastreams);
		MqttMessage message = new MqttMessage(SerializerCBOR.serialize(eventMessage));
		EDPSimulator.publish("oda/event/" + deviceId, message);
	}

	public class TestCallback implements MqttCallback {
		@Override
		public void connectionLost(Throwable throwable) {/* method not used*/}
		@Override
		public void messageArrived(String topic, MqttMessage message) throws IOException {
			EventResponseStruct response = SerializerJSON.deserialize(message.getPayload(), EventResponseStruct.class);
			for (int i = 0; i < datastreamIds.size(); i++) {
				if(response.isDatastream(datastreamIds.get(i))) {
					responseReceived.set(i, true);
					if (response.constainsValue(values.get(i), String.class)) {
						responseIsOk.set(i, true);
					}
				}
			}
		}
		@Override
		public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {/* method not used*/}
	}
}
