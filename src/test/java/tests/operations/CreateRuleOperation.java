package tests.operations;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import tests.dataStructs.general.ResponseFormat;
import tests.serializer.SerializerJSON;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

public class CreateRuleOperation {

	private MqttClient client = new MqttClient("tcp://localhost", "mqttClientCreateRule", new MemoryPersistence());

	private boolean responseIsOk;
	private boolean responseReceived;

	String deviceId;
	String datastreamId;
	String namerule;
	String when;
	String then;

	public CreateRuleOperation() throws MqttException {
		// This method is unimplemented because we need put an exception for the MqttClient
	}

	@Given("the device id of the rule we want to create: {string}")
	public void theDeviceIdOfTheRuleWeWantToCreate(String deviceId) {
		this.deviceId = deviceId;
	}

	@Given("the datastream id of the rule we want to create: {string}")
	public void theDatastreamIdOfTheRuleWeWantToCreate(String datastreamId) {
		this.datastreamId = datastreamId;
	}

	@Given("the name of the rule we want to create: {string}")
	public void theNameOfTheRuleWeWantToCreate(String namerule) {
		this.namerule = namerule;
	}

	@Given("a when instruction of the rule we want to create: {string}")
	public void aWhenInstructionOfTheRuleWeWantToCreate(String when) {
		this.when = when;
	}

	@Given("a then instruction of the rule we want to create: {string}")
	public void aThenInstructionOfTheRuleWeWantToCreate(String then) {
		this.then = then;
	}

	@When("I send a request to ODA with the rule")
	public void iSendARequestToODAWithTheRule() throws MqttException {
		client.connect();
		client.setCallback(new CreateRuleCallback());
		client.subscribe("odm/response/#");
		responseIsOk = false;
		responseReceived = false;
		String temp = "{\"operation\":{\"request\":{\"timestamp\":1550317726454,\"deviceId\":\"" + deviceId + "\"," +
				"\"name\":\"CREATE_RULE\",\"parameters\":[{\"name\":\"variableList\",\"value\":{\"array\":[{" +
				"\"datastreamId\":\"" + datastreamId + "\",\"namerule\":\"" + namerule + "\",\"when\":\"" + when + "\"," +
				"\"then\":\"" + then + "\"}]}}],\"id\":\"4aabb9c6-61ec-43ed-b0e4-dabface44b64\"}}}";
		MqttMessage message = new MqttMessage(temp.getBytes());
		client.publish("odm/request/" + deviceId, message);
	}

	@Then("I receive an OK message")
	public void iReceiveAnOKMessage() throws InterruptedException, MqttException {
		for(int i = 0; i < 10 && !responseReceived; i++) {
			TimeUnit.MILLISECONDS.sleep(1000);
		}
		client.disconnect();
		assertTrue(responseIsOk);
	}

	private class CreateRuleCallback implements MqttCallback {

		@Override
		public void connectionLost(Throwable throwable) {/*method not used*/}

		@Override
		public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
			ResponseFormat response = SerializerJSON.deserialize(mqttMessage.getPayload(), ResponseFormat.class);
			if (response.getOperation().getResponse().getSteps().get(0).getResult().equals("SUCCESSFUL")) {
				responseIsOk = true;
			}

			responseReceived = true;
		}

		@Override
		public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {/*method not used*/}
	}

	// '{"operation":{"request":{"timestamp":1550317726454,"deviceId":"<deviceId>","name":"<nameOperation>","parameters":[{"name":"variableList","value":{"array":[{"datastreamId":"<datastreamId>","namerule":"<namerule>","when":"<when>","then":"<then>"}]}}],"id":"4aabb9c6-61ec-43ed-b0e4-dabface44b64"}}}'
}
