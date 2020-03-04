package es.amplia.oda.connector.dnp3.utils;

import com.automatak.dnp3.ChannelListener;
import com.automatak.dnp3.enums.ChannelState;

public class DNP3ChannelListener implements ChannelListener {

	private ChannelState currentState = ChannelState.SHUTDOWN;

	@Override
	public void onStateChange(ChannelState state) {
		currentState = state;
	}

	public boolean isOpen() {
		return currentState.equals(ChannelState.OPEN);
	}
}
