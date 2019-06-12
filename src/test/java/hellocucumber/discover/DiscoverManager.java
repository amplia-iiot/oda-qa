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
	private MqttClient manager;
	private boolean connected = false;
	private static ArrayList<String> actives = new ArrayList<>();

	public DiscoverManager(String id) throws MqttException {
		manager = new MqttClient("tcp://localhost", id);
	}

	public void connect() throws MqttException {
		if(!connected) {
			manager.connect();
			connected = true;
		}
	}

	public void disconnect() throws MqttException {
		if(connected) {
			manager.disconnect();
			connected = false;
		}
	}

	public void enable(String device, String datastream, String mode) throws MqttException, IOException, InterruptedException {
		String toEnable = device + "/" + datastream;
		if (!actives.contains(toEnable)) {
			Enable enable = new Enable(mode);
			manager.publish("oda/enable/" + toEnable, new MqttMessage(SerializerCBOR.serialize(enable)));
			actives.add(toEnable);
			Thread.sleep(500);
		}
	}

	public void disable(String device, String datastream) throws MqttException {
		String toDisable = device + "/" + datastream;
		if (actives.contains(toDisable)) {
			manager.publish("oda/disable/" + toDisable, new MqttMessage(new byte[0]));
			actives.remove(toDisable);
		}
	}

	public void multiEnable(String device, ArrayList<Pair<String, String>> datastreams) throws MqttException, IOException {
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

	public void multiDisable(String device, ArrayList<String> datastreams) throws MqttException, IOException {
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

	public int connectedQuant() {
		return actives.size();
	}
}
