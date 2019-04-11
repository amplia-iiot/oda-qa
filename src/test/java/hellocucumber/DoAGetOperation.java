package hellocucumber;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.eclipse.paho.client.mqttv3.*;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

public class DoAGetOperation {


	private MqttClient client = new MqttClient("tcp://localhost", "123456");
	private MqttMessage message;

	private boolean responseReceived;
	private int value;

	public DoAGetOperation() throws MqttException {
		// This method is unimplemented because we need put a exception for the MqttClient
	}

	@Given("^a device where I want to get a data$")
	public void aDeviceWhereIWantToGetAData () {
		/* Importante guardar:
		 * {"operation":{"request":{"timestamp":1554978284595,"name":"GET_DEVICE_PARAMETERS","parameters":[{"name":"variableList","value":{"array":[{"variableName":"temp"}]}}],"id":"4aabb9c6-61ec-43ed-b0e4-dabface44b64"}}}
		 * Es el mensaje tipo que manda OpenGate que tienes que falsear.
		 */
		String temp = "{\"operation\":{\"request\":{\"timestamp\":1554978284595," +
						"\"name\":\"GET_DEVICE_PARAMETERS\"," +
							"\"parameters\":[{\"name\":\"variableList\"," +
								"\"value\":{\"array\":[{\"variableName\":\"temp\"}]}}]}}}";
		message = new MqttMessage(temp.getBytes());
	}

	@When("^I send a request to get the data$")
	public void iSendARequestToGetTheData () throws MqttException {
		client.connect();
		client.setCallback(new TestCallback());
		responseReceived = false;
		client.subscribe("odm/response/#");
		client.publish("odm/request/opc_test", message);
	}

	@Then("^I receive a data$")
	public void iReceiveAData () throws InterruptedException, MqttException {
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
			if(result.contains("")) {
				responseReceived = true;
			}
		}

		@Override
		public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
			// method not used
		}
	}
}
