package hellocucumber;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.concurrent.TimeUnit;

public class Stepdefs {
	// __________QA ODA__________
	MqttClient client = new MqttClient("tcp://localhost", "123456");

	MqttMessage message;

	public Stepdefs() throws MqttException {
	}

	@Given("^a mqtt message$")
	public void a_mqtt_message() throws MqttException {
		message = new MqttMessage("{\"operation\":{\"request\":{\"timestamp\":0,\"name\":\"DUMP_OPERATION\"}}}".getBytes());
	}

	@When("^I send to dispatcher$")
	public void i_send_to_dispatcher() throws MqttException {
		client.connect();
		client.publish("odm/request/opc_test", message);
	}

	@Then("^I receive a error$")
	public void iReceiveAError() throws MqttException {

		client.disconnect();
	}
}
