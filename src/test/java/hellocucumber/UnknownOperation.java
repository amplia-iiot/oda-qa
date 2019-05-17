package hellocucumber;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import hellocucumber.dataStructs.unknown.UnknownResponseStruct;
import hellocucumber.discover.DiscoverManager;
import hellocucumber.discover.DiscoverData;
import hellocucumber.serializer.SerializerJSON;
import org.eclipse.paho.client.mqttv3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

public class UnknownOperation {

	private MqttClient client = new MqttClient("tcp://localhost", "mqqtClientUnknownOp");
	private DiscoverManager discoverManager = new DiscoverManager("discoverManagerUnknownOp");

	private MqttMessage message;
	private boolean responseReceived;
	private boolean responseIsOk;

	public UnknownOperation() throws MqttException {
		// This method is unimplemented because we need put a exception for the MqttClient
	}

	@Given("^a mqtt message with a unkown operation$")
	public void aMqttMessageWithAUnkownOperation() {
		message = new MqttMessage("{\"operation\":{\"request\":{\"timestamp\":1554978284595,\"deviceId\":\"d\",\"name\":\"MET_DEVICE_PARAMETERS\",\"parameters\":[{\"name\":\"variableList\",\"value\":{\"array\":[{\"variableName\":\"d\"}]}}],\"id\":\"4aabb9c6-61ec-43ed-b0e4-dabface44b64\"}}}".getBytes());
	}

	@When("^I send the operation to dispatcher$")
	public void iSendTheOperationToDispatcher() throws MqttException, IOException {
		client.connect();
		discoverManager.connect();
		client.setCallback(new TestCallback());
		responseReceived = false;
		responseIsOk = false;
		client.subscribe("odm/response/#");
		discoverManager.enable("device","datastream","RD");
		client.publish("odm/request/" + DiscoverData.MAINDEVICEID, message);
	}

	@Then("^I receive a error$")
	public void iReceiveAError() throws MqttException, InterruptedException {
		for(int i = 0; i < 10 && !responseReceived; i++) {
			TimeUnit.MILLISECONDS.sleep(500);
		}
		discoverManager.disable("device","datastream");
		client.disconnect();
		discoverManager.disconnect();
		assertTrue(responseReceived);
		assertTrue(responseIsOk);
	}

	public class TestCallback implements MqttCallback {

		@Override
		public void connectionLost(Throwable throwable) {
			// method not used
		}

		@Override
		public void messageArrived(String topic, MqttMessage message) throws IOException {
			responseReceived = true;
			UnknownResponseStruct result = SerializerJSON.deserialize(message.getPayload(), UnknownResponseStruct.class);
			if(result.resultDescription().equals("Operation not supported by the device")) {
				responseIsOk = true;
			}
		}

		@Override
		public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
			// method not used
		}
	}
}