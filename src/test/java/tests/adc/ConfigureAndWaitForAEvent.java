package tests.adc;

import com.jcraft.jsch.JSchException;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import tests.dataStructs.event.EventResponseStruct;
import tests.dataStructs.general.ResponseFormat;
import tests.discover.DiscoverData;
import tests.jsch.CopyFile;
import tests.jsch.JschData;
import tests.serializer.SerializerJSON;

import javax.naming.ConfigurationException;
import java.io.File;
import java.io.IOException;
import java.sql.Time;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

public class ConfigureAndWaitForAEvent {
	private static final String hardwareCfg = "es.amplia.oda.hardware.diozero.cfg";
	private static final String datastreamsCfg = "es.amplia.oda.datastreams.adc.cfg";
	private static final String deviceCfg = "mpp5";
	private static final String newDeviceCfg = "mpp5_event";
	private static final String copyFormat = ".sc";
	private static final String routeToFiles = "./src/test/resources/tests/adc/event/";

	private MqttClient opengate = new MqttClient("tcp://localhost", "opengateExClient", new MemoryPersistence());

	JschData jschData = new JschData();
	DiscoverData discoverData = new DiscoverData();
	private boolean receivedFirstEvent;
	private boolean receivedSecondEvent;
	private boolean eventsAreOk;

	public ConfigureAndWaitForAEvent() throws MqttException, IOException, ConfigurationException {
		// Constructor to specify the exceptions of construction
	}

	@Given("a client connected with the ODA to receive its events")
	public void aClientConnectedWithTheODAToReceiveItsEvents() throws MqttException {
		this.opengate.connect();
		this.opengate.setCallback(new SimOpengateCallback());
		this.opengate.subscribe("odm/iot/#");

		this.receivedFirstEvent = false;
		this.receivedSecondEvent = false;
		this.eventsAreOk = true;
	}

	@Given("a file with the value of ADC")
	public void aFileWithTheValueOfADC() throws IOException, JSchException, ConfigurationException {
		CopyFile cf = new CopyFile(jschData.getSSH_USER_USER(), jschData.getSSH_USER_IP());
		cf.localToRemote(routeToFiles + deviceCfg, jschData.getPATH_CFG() + deviceCfg);
	}

	@When("I change the configuration of remote datastreams of ODA to to register a datastreamEvent")
	public void iChangeTheConfigurationOfRemoteDatastreamsOfODAToToRegisterADatastreamEvent() throws JSchException, ConfigurationException, IOException {
		CopyFile cf = new CopyFile(jschData.getSSH_USER_USER(), jschData.getSSH_USER_IP());
		cf.remoteToLocal(jschData.getPATH_CFG() + hardwareCfg, routeToFiles + hardwareCfg + copyFormat);
		cf.localToRemote(routeToFiles + hardwareCfg, jschData.getPATH_CFG() + hardwareCfg);
		cf.remoteToLocal(jschData.getPATH_CFG() + datastreamsCfg, routeToFiles + datastreamsCfg + copyFormat);
		cf.localToRemote(routeToFiles + datastreamsCfg, jschData.getPATH_CFG() + datastreamsCfg);
	}

	@When("after wait a while, I change the value of the input dramatically")
	public void afterWaitAWhileIChangeTheValueOfTheInputDramatically() throws InterruptedException, IOException, JSchException, ConfigurationException {
		TimeUnit.SECONDS.sleep(5);
		CopyFile cf = new CopyFile(jschData.getSSH_USER_USER(), jschData.getSSH_USER_IP());
		cf.localToRemote(routeToFiles + newDeviceCfg, jschData.getPATH_CFG() + deviceCfg);
	}

	@Then("ODA send to me a event that contains new data of input")
	public void odaSendToMeAEventThatContainsNewDataOfInput() throws IOException, JSchException, ConfigurationException, MqttException, InterruptedException {
		for(int i = 0; i < 10 && !receivedSecondEvent; i++) {
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

		assertTrue(eventsAreOk);
	}

	private class SimOpengateCallback implements MqttCallback {
		@Override
		public void connectionLost(Throwable throwable) {
			// Method not used
		}

		@Override
		public void messageArrived(String s, MqttMessage mqttMessage) throws InterruptedException {
			EventResponseStruct response;
			try {
				response = SerializerJSON.deserialize(mqttMessage.getPayload(), EventResponseStruct.class);
				if (!receivedFirstEvent) {
					if (!response.constainsValue(Double.valueOf(Float.valueOf(23001f / 1.8f / 1000000f * 10f).toString()), Double.class)) {
						eventsAreOk = false;
					}
					receivedFirstEvent = true;
				} else {
					if (!response.constainsValue(Double.valueOf(Float.valueOf(2300123f / 1.8f / 1000000f * 10f).toString()), Double.class)) {
						eventsAreOk = false;
					}
					receivedSecondEvent = true;
				}
			} catch (IOException e) {
				System.out.println("Error parsing message received from ODA");
			}
		}

		@Override
		public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
			// Method not used
		}
	}
}
