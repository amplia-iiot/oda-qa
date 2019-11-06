package tests.iec104.utils;

import es.amplia.oda.connector.iec104.types.BitstringCommand;
import es.amplia.oda.connector.iec104.types.BytestringPointInformationSequence;
import es.amplia.oda.connector.iec104.types.BytestringPointInformationSingle;
import io.netty.channel.ChannelHandlerContext;
import org.eclipse.neoscada.protocol.iec60870.asdu.message.InterrogationCommand;
import org.eclipse.neoscada.protocol.iec60870.client.data.DataModuleHandler;
import org.eclipse.neoscada.protocol.iec60870.client.data.DataModuleOptions;

public class QADataModuleHandler extends DataModuleHandler {

	private QADataHandler dataHandler;

	QADataModuleHandler(QADataHandler dataHandler, DataModuleOptions options) {
		super(dataHandler, options);
		this.dataHandler = dataHandler;
	}

	@Override
	public void channelRead (final ChannelHandlerContext ctx, final Object msg ) throws Exception
	{
		if (msg instanceof InterrogationCommand) {
			handleInterrogationAnswer();
		}
		else if (msg instanceof BitstringCommand) {
			handleInterrogationAnswer();
		}
		else if ( msg instanceof BytestringPointInformationSingle)
		{
			handleDataMessage ( (BytestringPointInformationSingle)msg );
		}
		else if ( msg instanceof BytestringPointInformationSequence )
		{
			handleDataMessage ( (BytestringPointInformationSequence)msg );
		} else {
			super.channelRead(ctx, msg);
		}
	}

	private void handleDataMessage(BytestringPointInformationSingle msg) {
		this.dataHandler.process(msg);
	}

	private void handleDataMessage(BytestringPointInformationSequence msg) {
		this.dataHandler.process(msg);
	}

	private void handleInterrogationAnswer() {
		this.dataHandler.interrogatedCorrectly();
	}
}
