package tests.iec104;

import es.amplia.oda.connector.iec104.types.BitstringCommand;
import tests.iec104.utils.QAAbstractDataProcessor;
import io.netty.channel.ChannelHandlerContext;
import org.eclipse.neoscada.protocol.iec60870.asdu.ASDUHeader;
import org.eclipse.neoscada.protocol.iec60870.asdu.types.*;
import org.eclipse.neoscada.protocol.iec60870.client.data.DataModuleContext;

public abstract class CommandDataHandler extends QAAbstractDataProcessor {

	private boolean interrogated;
 	private boolean responseReceived;
	private boolean responseIsOk;
	private boolean connectionAchieved;

	private DataModuleContext dataModuleContext;
	private ChannelHandlerContext channelHandlerContext;

	@Override
	protected abstract void fireEntry(ASDUAddress asduAddress, InformationObjectAddress informationObjectAddress, Value<?> value);

	@Override
	public abstract void interrogatedCorrectly();

	@Override
	public void activated(DataModuleContext dataModuleContext, ChannelHandlerContext channelHandlerContext) {
		dataModuleContext.requestStartData();
		this.dataModuleContext = dataModuleContext;
		this.channelHandlerContext = channelHandlerContext;
	}

	@Override
	public void started() {
		System.out.println("Hey, this happens");
	}

	@Override
	public void disconnected() {
	}

	void interrogate() {
		dataModuleContext.startInterrogation(ASDUAddress.valueOf(0), QualifierOfInterrogation.GLOBAL);
	}

	void bitstringCommand(int index, byte[] bitestring) {
		channelHandlerContext.writeAndFlush(new BitstringCommand(
				new ASDUHeader(CauseOfTransmission.ACTIVATED, ASDUAddress.valueOf(0)),
				InformationObjectAddress.valueOf(index), bitestring));
	}

	boolean isInterrogated() {
		return interrogated;
	}

	void setInterrogated(boolean interrogated) {
		this.interrogated = interrogated;
	}

	public boolean isResponseReceived() {
		return responseReceived;
	}

	public void setResponseReceived(boolean responseReceived) {
		this.responseReceived = responseReceived;
	}

	public boolean isResponseIsOk() {
		return responseIsOk;
	}

	public void setResponseIsOk(boolean responseIsOk) {
		this.responseIsOk = responseIsOk;
	}

	boolean isConnectionAchieved() {
		return connectionAchieved;
	}

	void setConnectionAchieved(boolean connectionAchieved) {
		this.connectionAchieved = connectionAchieved;
	}
}
