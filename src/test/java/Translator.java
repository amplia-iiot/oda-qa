import cucumber.api.java.en.Then;
import hellocucumber.dataStructs.discover.Enable;
import hellocucumber.dataStructs.general.ResponseFormat;
import hellocucumber.dataStructs.read.ReadRequestStruct;
import hellocucumber.dataStructs.read.ReadResponseStruct;
import hellocucumber.serializer.SerializerCBOR;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.IOException;

public class Translator {
	@Then("^Translate messages from CBOR$")
	public void translateMessagesFromCBOR() throws MqttException {
		MqttClient translator = new MqttClient("tcp://localhost", "Stalker", new MemoryPersistence());
		translator.connect();
		translator.subscribe("oda/#");
		translator.setCallback(new StalkerCallback());
		while(true) {
			// Im waiting for a stop signal from Kernel
		}
	}

	public class StalkerCallback implements MqttCallback {
		@Override
		public void connectionLost(Throwable throwable) {/* method not used*/}
		@Override
		public void messageArrived(String topic, MqttMessage message) throws IOException {
			try {
				ResponseFormat response = SerializerCBOR.deserialize(message.getPayload(), ResponseFormat.class);
				System.out.println(response);
			} catch (Exception e) {
				// Stalker do nothing
			}
			try {
				Enable response = SerializerCBOR.deserialize(message.getPayload(), Enable.class);
				System.out.println(topic + ": " + response.getMode());
			} catch (Exception e) {
				// Stalker do nothing
			}
			try {
				ReadRequestStruct response = SerializerCBOR.deserialize(message.getPayload(), ReadRequestStruct.class);
				System.out.println(topic + ": Request: " + response.getId());
			} catch (Exception e) {
				// Stalker do nothing
			}
			try {
				ReadResponseStruct response = SerializerCBOR.deserialize(message.getPayload(), ReadResponseStruct.class);
				System.out.println(topic + ": Response: " + response.getId() + " ; " + response.getValue());
			} catch (Exception e) {
				// Stalker do nothing
			}

		}
		@Override
		public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {/* method not used*/}
	}
}
