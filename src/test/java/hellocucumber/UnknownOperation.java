package hellocucumber;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.eclipse.paho.client.mqttv3.*;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

public class UnknownOperation {

	private MqttClient client = new MqttClient("tcp://localhost", "123456");
	private MqttMessage message;
	private boolean responseReceived;

	public UnknownOperation() throws MqttException {
		// This method is unimplemented because we need put a exception for the MqttClient
	}

	@Given("^a mqtt message with a unkown operation$")
	public void aMqttMessageWithAUnkownOperation() {
		message = new MqttMessage("{\"operation\":{\"request\":{\"timestamp\":0,\"name\":\"DUMP_OPERATION\"}}}".getBytes());
	}

	@When("^I send the operation to dispatcher$")
	public void iSendTheOperationToDispatcher() throws MqttException {
		client.connect();
		client.setCallback(new TestCallback());
		responseReceived = false;
		client.subscribe("odm/response/#");
		client.publish("odm/request/opc_test", message);
	}

	@Then("^I receive a error$")
	public void iReceiveAError() throws MqttException, InterruptedException {
		for(int i = 0; i < 5 && !responseReceived; i++) {
			TimeUnit.SECONDS.sleep(1);
		}
		assertTrue(responseReceived);
		client.disconnect();
	}

	public class TestCallback implements MqttCallback {

		@Override
		public void connectionLost(Throwable throwable) {
			// method not used
		}

		@Override
		public void messageArrived(String topic, MqttMessage message) {
			String result = message.toString();
			if(result.contains("Operation not supported by the device")) {
				responseReceived = true;
			}
		}

		@Override
		public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
			// method not used
		}
	}
}
