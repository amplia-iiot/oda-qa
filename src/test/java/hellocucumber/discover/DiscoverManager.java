package hellocucumber.discover;

import hellocucumber.dataStructs.discover.*;
import hellocucumber.serializer.SerializerCBOR;
import javafx.util.Pair;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.IOException;
import java.util.ArrayList;

public class DiscoverManager {
	private static MqttClient manager;
	private static boolean connected = false;

	static {
		try {
			manager = new MqttClient("tcp://localhost", "discoverManager");
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}

	private static ArrayList<String> actives = new ArrayList<>();

	public static void connect() throws MqttException {
		if(!connected) {
			manager.connect();
			connected = true;
		}
	}

	public static void disconnect() throws MqttException {
		if(connected) {
			manager.disconnect();
			connected = false;
		}
	}

	public static void enable(String device, String datastream, String mode) throws MqttException, IOException {
		String toEnable = device + "/" + datastream;
		if (!actives.contains(toEnable)) {
			Enable enable = new Enable(mode);
			manager.publish("oda/enable/" + toEnable, new MqttMessage(SerializerCBOR.serialize(enable)));
			actives.add(toEnable);
		}
	}

	public static void disable(String device, String datastream) throws MqttException {
		String toDisable = device + "/" + datastream;
		if (actives.contains(toDisable)) {
			manager.publish("oda/disable/" + toDisable, new MqttMessage(new byte[0]));
			actives.remove(toDisable);
		}
	}

	public static void multiEnable(String device, ArrayList<Pair<String, String>> datastreams) throws MqttException, IOException {
		boolean isOk = true;
		Multienable multienable = new Multienable();
		for (Pair pair: datastreams) {
			if(!actives.contains(device + "/" + pair.getKey())) {
				multienable.addDatastream(new EnablingDatastreams(pair.getKey().toString(), pair.getValue().toString()));
			} else {
				isOk = false;
			}
		}

		if(isOk) {
			for (Pair pair: datastreams) {
				actives.add(device + "/" + pair.getKey());
			}
			manager.publish("oda/enable/" + device, new MqttMessage(SerializerCBOR.serialize(multienable)));
		}

	}

	public static void multiDisable(String device, ArrayList<String> datastreams) throws MqttException, IOException {
		boolean isOk = true;
		Multidisable multidisable = new Multidisable();
		for (String ds: datastreams) {
			if(actives.contains(device + "/" + ds)) {
				multidisable.addDatastream(new DisablingDatastreams(ds));
			} else {
				isOk = false;
			}
		}

		if(isOk) {
			for (String ds: datastreams) {
				actives.remove(device + "/" + ds);
			}
			manager.publish("oda/disable/" + device, new MqttMessage(SerializerCBOR.serialize(multidisable)));
		}
	}
}
