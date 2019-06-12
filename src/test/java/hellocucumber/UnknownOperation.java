package hellocucumber;

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

import static org.junit.Assert.assertTrue;

public class UnknownOperation {
	private MqttClient client = new MqttClient("tcp://localhost", "mqqtClientUnknown", new MemoryPersistence());
	private DiscoverManager discoverManager = new DiscoverManager("discoverManagerUnknownOp");
	private final DiscoverData discoverData = new DiscoverData();

	private MqttMessage message;
	private boolean responseReceived;
	private boolean responseIsOk;

	public UnknownOperation() throws MqttException, IOException, ConfigurationException {
		// This method is unimplemented because we need put a exception for the MqttClient
	}

	@Given("^a mqtt message with a unkown operation$")
	public void aMqttMessageWithAUnkownOperation() {
		message = new MqttMessage(("{\"operation\":{\"request\":{\"timestamp\":1554978284595,\"deviceId\":\"edp\",\"name\":\"MET_DEVICE_PARAMETERS\",\"parameters\":[{\"name\":\"variableList\",\"value\":{\"array\":[{\"variableName\":\"q\"}]}}],\"id\":\"4aabb9c6-61ec-43ed-b0e4-dabface44b64\"}}}").getBytes());
	}

	@Given("A started EDP simulator to do an unknown operation")
	public void aStartedEDPSimulatorToDoAnUnknownOperation() throws MqttException, IOException, InterruptedException {
		discoverManager.connect();
		discoverManager.enable("device","datastream","RD");
	}

	@When("^I send the operation to dispatcher$")
	public void iSendTheOperationToDispatcher() throws MqttException, IOException, InterruptedException {
		client.connect();
		client.setCallback(new TestCallback());
		responseReceived = false;
		responseIsOk = false;
		client.subscribe("odm/response/#");
		client.publish("odm/request/" + discoverData.getMAINDEVICEID(), message);
	}

	@Then("^I receive a error$")
	public void iReceiveAError() throws MqttException, InterruptedException {
		for(int i = 0; i < 10 && !responseReceived; i++) {
			TimeUnit.MILLISECONDS.sleep(500);
		}
		client.disconnect();
		assertTrue(responseIsOk);
	}

	@Then("I disconnect the simulator")
	public void iDisconnectTheSimulator() throws MqttException {
		discoverManager.disable("device","datastream");
		discoverManager.disconnect();
	}

	public class TestCallback implements MqttCallback {

		@Override
		public void connectionLost(Throwable throwable) {
			// method not used
		}

		@Override
		public void messageArrived(String topic, MqttMessage message) throws IOException {
			ResponseFormat result = SerializerJSON.deserialize(message.getPayload(), ResponseFormat.class);
			if(result.resultDescription().equals("Operation not supported by the device")) {
				responseIsOk = true;
			}
			responseReceived = true;
		}

		@Override
		public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
			// method not used
		}
	}
}