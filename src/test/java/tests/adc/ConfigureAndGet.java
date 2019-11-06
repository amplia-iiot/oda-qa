package tests.adc;

import com.jcraft.jsch.JSchException;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import tests.dataStructs.general.ResponseFormat;
import tests.discover.DiscoverData;
import tests.jsch.CopyFile;
import tests.jsch.JschData;
import tests.serializer.SerializerJSON;

import javax.naming.ConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

public class ConfigureAndGet {
	private static final String hardwareCfg = "es.amplia.oda.hardware.diozero.cfg";
	private static final String datastreamsCfg = "es.amplia.oda.datastreams.adc.cfg";
	private static final String deviceCfg = "mpp5";
	private static final String copyFormat = ".sc";
	private static final String routeToFiles = "./src/test/resources/tests/adc/get/";

	private MqttClient opengate = new MqttClient("tcp://localhost", "opengateExClient", new MemoryPersistence());

	JschData jschData = new JschData();
	DiscoverData discoverData = new DiscoverData();
	private boolean responseReceived;
	private boolean responseIsOk;

	public ConfigureAndGet() throws MqttException, IOException, ConfigurationException {
		// Constructor to specify the Exception on create
	}

	@Given("a client connected with the ODA to receive its responses")
	public void aClientConnectedWithTheODAToReceiveItsResponses() throws MqttException {
		this.opengate.connect();
		this.opengate.setCallback(new SimOpengateCallback());
		this.opengate.subscribe("odm/response/#");

		this.responseReceived = false;
		this.responseIsOk = false;
	}

	@When("I change the configuration of remote datastreams of ODA to local configuration")
	public void iChangeTheConfigurationOfRemoteDatastreamsOfODAToLocalConfiguration() throws JSchException, ConfigurationException, IOException {
		CopyFile cf = new CopyFile(jschData.getSSH_USER_USER(), jschData.getSSH_USER_IP());
		cf.localToRemote(routeToFiles + deviceCfg, jschData.getPATH_CFG() + deviceCfg);
		cf.remoteToLocal(jschData.getPATH_CFG() + hardwareCfg, routeToFiles + hardwareCfg + copyFormat);
		cf.localToRemote(routeToFiles + hardwareCfg, jschData.getPATH_CFG() + hardwareCfg);
		cf.remoteToLocal(jschData.getPATH_CFG() + datastreamsCfg, routeToFiles + datastreamsCfg + copyFormat);
		cf.localToRemote(routeToFiles + datastreamsCfg, jschData.getPATH_CFG() + datastreamsCfg);
	}

	@When("I send a discover operation")
	public void iSendADiscoverOperation() throws MqttException, InterruptedException {
		TimeUnit.SECONDS.sleep(5);
		String temp = "{\"operation\":{\"request\":{\"timestamp\":1554978284595,\"deviceId\":\"" + "" + "\",\"name\":\"GET_DEVICE_PARAMETERS\"," +
				"\"parameters\":[{\"name\":\"variableList\",\"value\":{\"array\":[{\"variableName\":\"" + "aDatastream" +
				"\"}]}}],\"id\":\"4aabb9c6-61ec-43ed-b0e4-dabface44b64\"}}}";
		opengate.publish("odm/request/" + discoverData.getMAINDEVICEID(), new MqttMessage(temp.getBytes()));
	}

	@Then("ODA send to me a discovery that contains configured datastreams")
	public void odaSendToMeADiscoveryThatContainsConfiguredDatastreams() throws InterruptedException, MqttException, JSchException, ConfigurationException, IOException {
		for(int i = 0; i < 10 && !responseReceived; i++) {
			TimeUnit.MILLISECONDS.sleep(500);
		}
		if(opengate.isConnected()) {
			opengate.disconnect();
		}
		CopyFile cf = new CopyFile(jschData.getSSH_USER_USER(), jschData.getSSH_USER_IP());
		cf.localToRemote(routeToFiles + hardwareCfg + copyFormat, jschData.getPATH_CFG() + hardwareCfg);
		cf.localToRemote(routeToFiles + datastreamsCfg + copyFormat, jschData.getPATH_CFG() + datastreamsCfg);
		File toDelete = new File(routeToFiles + hardwareCfg + copyFormat);
		toDelete.delete();
		toDelete = new File(routeToFiles + datastreamsCfg + copyFormat);
		toDelete.delete();

		assertTrue(responseIsOk);
	}

	private class SimOpengateCallback implements MqttCallback {
		@Override
		public void connectionLost(Throwable throwable) {
			/* not used */
		}

		@Override
		public void messageArrived(String s, MqttMessage mqttMessage) {
			ResponseFormat response;
			try {
				response = SerializerJSON.deserialize(mqttMessage.getPayload(), ResponseFormat.class);
				responseIsOk = response.responseValue().equals(Double.valueOf(Float.valueOf(23001f / 1.8f / 1000000f * 10f).toString()));
			} catch (Exception e) {
				System.out.println("Error parsing message received from ODA");
			}
			responseReceived = true;
		}

		@Override
		public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
			/* not used */
		}
	}
}
