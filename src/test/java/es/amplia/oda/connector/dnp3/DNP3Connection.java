package es.amplia.oda.connector.dnp3;

import com.automatak.dnp3.*;
import com.automatak.dnp3.impl.DNP3ManagerFactory;
import com.automatak.dnp3.mock.DefaultMasterApplication;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import es.amplia.oda.connector.dnp3.configuration.DNP3Configuration;
import es.amplia.oda.connector.dnp3.utils.DNP3ChannelListener;
import es.amplia.oda.connector.dnp3.utils.DNP3LogHandler;

import javax.naming.ConfigurationException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

public class DNP3Connection {
	static boolean loaded = false;
	static UnsatisfiedLinkError loadException = null;
	static {
		System.setProperty("com.automatak.dnp3.nostaticload", "");
		File f = new File("src/test/resources/es/amplia/oda/connector/dnp3/libraries/");
		System.setProperty("java.library.path", f.getAbsolutePath());
		try {
			Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
			fieldSysPath.setAccessible(true);
			fieldSysPath.set(null,null);
			try {
				System.loadLibrary("openpal");
				System.loadLibrary("opendnp3");
				System.loadLibrary("asiopal");
				System.loadLibrary("asiodnp3");
				System.loadLibrary("opendnp3java");
				loaded = true;
			} catch (UnsatisfiedLinkError e) {
				loadException = e;
			}
		} catch (NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	private final DNP3Manager manager = DNP3ManagerFactory.createManager(1, new DNP3LogHandler());

	public DNP3Connection() throws DNP3Exception {}

	private DNP3ChannelListener channelListener;
	private Channel channel;
	private DNP3Configuration config;
	private boolean started;
	private Master master;

	@Given("loaded dnp3 libraries")
	public void loadedDnp3Libraries() {
		if(!loaded) {
			System.out.println(loadException.getMessage());
		}
		assertTrue(loaded);
	}

	@Given("data for the connection")
	public void dataForTheConnection() throws IOException, ConfigurationException {
		started = false;
		config = new DNP3Configuration();
	}

	@When("start the connection with ODA")
	public void startTheConnectionWithODA() throws DNP3Exception {
		channelListener = new DNP3ChannelListener();
		channel = manager.addTCPClient(
				config.getChannelIdentifier(),
				config.getLogLevel() | LogMasks.APP_COMMS,
				ChannelRetry.getDefault(),
				config.getIpAddress(),
				"0.0.0.0",
				config.getIpPort(),
				channelListener
		);
		master = channel.addMaster("master", new TestingSOEHandler(), DefaultMasterApplication.getInstance(),
				new MasterStackConfig());
		master.addPeriodicScan(Duration.ofSeconds(1), Header.getIntegrity());
		master.enable();
	}

	@Then("connection is achieved")
	public void connectionIsAchieved() throws InterruptedException {
		for(int i = 0; i < 10 && !isConnectionStarted(); i++) {
			TimeUnit.MILLISECONDS.sleep(500);
		}
		assertTrue(channelListener.isOpen());
	}

	private boolean isConnectionStarted() {
		return started;
	}

	public class TestingSOEHandler implements SOEHandler {

		@Override
		public void start() {
			started = true;
		}

		@Override
		public void end() {
			// Unused for this test
		}

		@Override
		public void processBI(HeaderInfo info, Iterable<IndexedValue<BinaryInput>> values) {
			// Unused for this test
		}

		@Override
		public void processDBI(HeaderInfo info, Iterable<IndexedValue<DoubleBitBinaryInput>> values) {
			// Unused for this test
		}

		@Override
		public void processAI(HeaderInfo info, Iterable<IndexedValue<AnalogInput>> values) {
			// Unused for this test
		}

		@Override
		public void processC(HeaderInfo info, Iterable<IndexedValue<Counter>> values) {
			// Unused for this test
		}

		@Override
		public void processFC(HeaderInfo info, Iterable<IndexedValue<FrozenCounter>> values) {
			// Unused for this test
		}

		@Override
		public void processBOS(HeaderInfo info, Iterable<IndexedValue<BinaryOutputStatus>> values) {
			// Unused for this test
		}

		@Override
		public void processAOS(HeaderInfo info, Iterable<IndexedValue<AnalogOutputStatus>> values) {
			// Unused for this test
		}

		@Override
		public void processDNPTime(HeaderInfo info, Iterable<DNPTime> values) {
			// Unused for this test
		}
	}
}
