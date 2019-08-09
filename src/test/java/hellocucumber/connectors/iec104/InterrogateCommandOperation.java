package hellocucumber.connectors.iec104;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import hellocucumber.connectors.iec104.utils.QADataModule;
import org.eclipse.neoscada.protocol.iec60870.ProtocolOptions;
import org.eclipse.neoscada.protocol.iec60870.asdu.types.*;
import org.eclipse.neoscada.protocol.iec60870.client.AutoConnectClient;
import org.eclipse.neoscada.protocol.iec60870.client.ClientModule;
import org.eclipse.neoscada.protocol.iec60870.client.data.DataModuleOptions;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertTrue;

public class InterrogateCommandOperation extends CommandDataHandler {

	private AutoConnectClient client;
	private ClientModule clientModule;
	private QADataModule dataModule;


	@Given("An IEC client connected to ODA server channel")
	public void anIECClientConnectedToODAServerChannel() {
		DataModuleOptions.Builder optionsModuleBuilder = new DataModuleOptions.Builder();

		ProtocolOptions.Builder optionsBuilder = new ProtocolOptions.Builder();
		optionsBuilder.setTimeout1(10000);
		optionsBuilder.setTimeout2(10000);
		optionsBuilder.setTimeout3(10000);
		ProtocolOptions options = optionsBuilder.build();

		dataModule = new QADataModule(this, optionsModuleBuilder.build());
		clientModule = new QAClientModule(optionsModuleBuilder.build(), options);
		final AutoConnectClient.ModulesFactory factory = () -> Arrays.asList(dataModule, clientModule);

		AutoConnectClient.StateListener listener = new AutoConnectClient.StateListener() {
			@Override
			public void stateChanged(AutoConnectClient.State state, Throwable throwable) {
				if(state.equals(AutoConnectClient.State.CONNECTED))
					setConnectionAchieved(true);
				else if(state.equals(AutoConnectClient.State.DISCONNECTED))
					setConnectionAchieved(false);
			}
		};

		this.client = new AutoConnectClient("172.19.17.116", 2404, options, factory, listener);

		setInterrogated(false);
		setResponseReceived(false);
		setResponseIsOk(false);
		setConnectionAchieved(false);
	}

	@When("I send a interrogation ASDU to ODA")
	public void iSendAInterrogationASDUToODA() throws InterruptedException {
		await().until(this::isConnectionAchieved);
		super.interrogate();
		setInterrogated(true);
	}

	@Then("I receive a sequential data that match with ASDU form")
	public void iReceiveASequentialDataThatMatchWithASDUForm() throws Exception {
		for(int i = 0; i < 10 && !isResponseReceived(); i++) {
			TimeUnit.MILLISECONDS.sleep(500);
		}
		clientModule.dispose();
		client.close();
		assertTrue(isResponseIsOk());
		assertTrue(isInterrogated());
	}

	@Override
	public void fireEntry(ASDUAddress asduAddress, InformationObjectAddress informationObjectAddress, Value<?> value) {
		System.out.println(asduAddress + " with origin " + informationObjectAddress + ":\n" + value);
		if (!value.getValue().getClass().equals(Boolean.class)
				&& asduAddress != null
				&& informationObjectAddress != null
				&& isResponseReceived()) {
			setResponseIsOk(true);
		}
	}

	@Override
	public void interrogatedCorrectly() {
		setResponseReceived(true);
	}
}
