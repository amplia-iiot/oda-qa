package es.amplia.oda.connector.iec104;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import es.amplia.oda.connector.iec104.utils.QADataModule;
import org.eclipse.neoscada.protocol.iec60870.ProtocolOptions;
import org.eclipse.neoscada.protocol.iec60870.asdu.types.*;
import org.eclipse.neoscada.protocol.iec60870.client.AutoConnectClient;
import org.eclipse.neoscada.protocol.iec60870.client.ClientModule;
import org.eclipse.neoscada.protocol.iec60870.client.data.DataModuleOptions;
import utils.jsch.JschData;

import javax.naming.ConfigurationException;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertTrue;

public class BitstringCommandOperation extends CommandDataHandler{

	private AutoConnectClient client;
	private ClientModule clientModule;
	private QADataModule dataModule;

	@Given("An IEC client connected to ODA server channel to use a bitstring command")
	public void anIECClientConnectedToODAServerChannelToUseABitstringCommand() throws IOException, ConfigurationException {
		DataModuleOptions.Builder optionsModuleBuilder = new DataModuleOptions.Builder();

		ProtocolOptions.Builder optionsBuilder = new ProtocolOptions.Builder();
		optionsBuilder.setTimeout1(10000);
		optionsBuilder.setTimeout2(10000);
		optionsBuilder.setTimeout3(10000);
		ProtocolOptions options = optionsBuilder.build();

		dataModule = new QADataModule(this, optionsModuleBuilder.build());
		clientModule = new QAClientModule(optionsModuleBuilder.build(), options);
		final AutoConnectClient.ModulesFactory factory = () -> Arrays.asList(dataModule, clientModule);

		AutoConnectClient.StateListener listener = (state, throwable) -> {
			if(state.equals(AutoConnectClient.State.CONNECTED))
				setConnectionAchieved(true);
			else if(state.equals(AutoConnectClient.State.DISCONNECTED))
				setConnectionAchieved(false);
		};

		JschData jschData = new JschData();
		this.client = new AutoConnectClient(jschData.getSSH_SERVER_IP(), 2404, options, factory, listener);

		setInterrogated(false);
		setResponseReceived(false);
		setConnectionAchieved(false);
	}

	@When("I send a bitstring command to ODA")
	public void iSendABitstringCommandToODA() {
		await().until(this::isConnectionAchieved);

		byte[] bytestring = new byte[] {(byte)(42 >> 24),
				(byte)(42 >> 16),
				(byte)(42 >> 8),
				(byte)(42)};

		super.bitstringCommand(10015,bytestring);

		setInterrogated(true);
	}

	@Then("I receive a sequential data that match with ASDU form from bitstring command")
	public void iReceiveASequentialDataThatMatchWithASDUFormFromBitstringCommand() throws Exception {
		for(int i = 0; i < 10 && !isResponseReceived(); i++) {
			TimeUnit.MILLISECONDS.sleep(500);
		}
		clientModule.dispose();
		client.close();
		assertTrue(isInterrogated());
		assertTrue(isConnectionAchieved());
	}

	@Override
	public void fireEntry(ASDUAddress asduAddress, InformationObjectAddress informationObjectAddress, Value<?> value) {
		System.out.println(asduAddress + " with origin " + informationObjectAddress + ":\n" + value);
	}

	@Override
	public void interrogatedCorrectly() {
		setResponseReceived(true);
	}
}
