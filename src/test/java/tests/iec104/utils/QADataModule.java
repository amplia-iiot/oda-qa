package tests.iec104.utils;

import io.netty.channel.socket.SocketChannel;
import org.eclipse.neoscada.protocol.iec60870.apci.MessageChannel;
import org.eclipse.neoscada.protocol.iec60870.asdu.MessageManager;
import org.eclipse.neoscada.protocol.iec60870.asdu.message.MessageRegistrator;
import org.eclipse.neoscada.protocol.iec60870.client.Client;
import org.eclipse.neoscada.protocol.iec60870.client.ClientModule;
import org.eclipse.neoscada.protocol.iec60870.client.data.DataModuleOptions;

public class QADataModule implements ClientModule {

	private final QADataHandler dataHandler;
	private final DataModuleOptions options;

	public QADataModule ( final QADataHandler dataHandler, final DataModuleOptions options )
	{
		this.dataHandler = dataHandler;
		this.options = options;
	}

	@Override
	public void initializeClient ( final Client client, final MessageManager manager )
	{
		new MessageRegistrator().register ( manager );
	}

	@Override
	public void initializeChannel ( final SocketChannel channel, final MessageChannel messageChannel )
	{
		channel.pipeline ().addLast ( new QADataModuleHandler( this.dataHandler, this.options ) );
	}

	@Override
	public void dispose ()
	{
	}

}
