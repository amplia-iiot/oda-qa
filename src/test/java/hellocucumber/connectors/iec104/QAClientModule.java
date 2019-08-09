package hellocucumber.connectors.iec104;

import es.amplia.oda.connector.iec104.codecs.*;
import es.amplia.oda.connector.iec104.types.BitstringCommand;
import es.amplia.oda.connector.iec104.types.BytestringPointInformationSequence;
import es.amplia.oda.connector.iec104.types.BytestringPointInformationSingle;
import io.netty.channel.ChannelHandler;
import io.netty.channel.socket.SocketChannel;
import org.eclipse.neoscada.protocol.iec60870.ProtocolOptions;
import org.eclipse.neoscada.protocol.iec60870.apci.MessageChannel;
import org.eclipse.neoscada.protocol.iec60870.asdu.MessageManager;
import org.eclipse.neoscada.protocol.iec60870.asdu.message.*;
import org.eclipse.neoscada.protocol.iec60870.asdu.types.ASDU;
import org.eclipse.neoscada.protocol.iec60870.client.Client;
import org.eclipse.neoscada.protocol.iec60870.client.ClientModule;
import org.eclipse.neoscada.protocol.iec60870.client.data.DataModule;
import org.eclipse.neoscada.protocol.iec60870.client.data.DataModuleHandler;
import org.eclipse.neoscada.protocol.iec60870.client.data.DataModuleOptions;

public class QAClientModule implements ClientModule {

	private DataModuleOptions dataModuleOptions;
	private ProtocolOptions protocolOptions;
	private MessageManager messageManager;

	QAClientModule(DataModuleOptions dataModuleOptions, ProtocolOptions protocolOptions) {
		this.dataModuleOptions = dataModuleOptions;
		this.protocolOptions = protocolOptions;
	}

	@Override
	public void initializeClient(Client client, MessageManager messageManager) {
		this.messageManager = messageManager;
		this.messageManager.registerCodec(SinglePointInformationSingle.class.getAnnotation(ASDU.class).id(),
				SinglePointInformationSingle.class.getAnnotation(ASDU.class).informationStructure(),
				new SinglePointSingleCodec());
		this.messageManager.registerCodec(SinglePointInformationSequence.class.getAnnotation(ASDU.class).id(),
				SinglePointInformationSequence.class.getAnnotation(ASDU.class).informationStructure(),
				new SinglePointSequenceCodec());
		this.messageManager.registerCodec(BytestringPointInformationSingle.class.getAnnotation(ASDU.class).id(),
				BytestringPointInformationSingle.class.getAnnotation(ASDU.class).informationStructure(),
				new BytestringPointSingleCodec());
		this.messageManager.registerCodec(BytestringPointInformationSequence.class.getAnnotation(ASDU.class).id(),
				BytestringPointInformationSequence.class.getAnnotation(ASDU.class).informationStructure(),
				new BytestringPointSequenceCodec());
		this.messageManager.registerCodec(MeasuredValueScaledSingle.class.getAnnotation(ASDU.class).id(),
				MeasuredValueScaledSingle.class.getAnnotation(ASDU.class).informationStructure(),
				new MeasuredValueScaledSingleCodec());
		this.messageManager.registerCodec(MeasuredValueScaledSequence.class.getAnnotation(ASDU.class).id(),
				MeasuredValueScaledSequence.class.getAnnotation(ASDU.class).informationStructure(),
				new MeasuredValueScaledSequenceCodec());
		this.messageManager.registerCodec(InterrogationCommand.class.getAnnotation(ASDU.class).id(),
				InterrogationCommand.class.getAnnotation(ASDU.class).informationStructure(),
				new InterrogationCommandCodec());
		this.messageManager.registerCodec(BitstringCommand.class.getAnnotation(ASDU.class).id(),
				BitstringCommand.class.getAnnotation(ASDU.class).informationStructure(),
				new BitstringCommandCodec());
	}

	@Override
	public void initializeChannel(SocketChannel socketChannel, MessageChannel messageChannel) {
		QAMessageChannel qaMessageChannel = new QAMessageChannel(this.protocolOptions, this.messageManager);
		socketChannel.pipeline().addLast(new ChannelHandler[]{qaMessageChannel});
	}

	@Override
	public void dispose() {

	}
}
