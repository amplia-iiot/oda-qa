package hellocucumber.connectors.iec104.utils;

import es.amplia.oda.connector.iec104.types.BytestringPointInformationSequence;
import es.amplia.oda.connector.iec104.types.BytestringPointInformationSingle;
import org.eclipse.neoscada.protocol.iec60870.asdu.types.InformationEntry;
import org.eclipse.neoscada.protocol.iec60870.asdu.types.InformationObjectAddress;
import org.eclipse.neoscada.protocol.iec60870.asdu.types.Value;
import org.eclipse.neoscada.protocol.iec60870.client.data.AbstractDataProcessor;

import java.util.Iterator;

public abstract class QAAbstractDataProcessor extends AbstractDataProcessor implements QADataHandler {

	public void process(BytestringPointInformationSingle msg) {
		if (!this.checkIgnore(msg)) {

			for (InformationEntry<byte[]> informationEntry : msg.getEntries()) {
				InformationEntry<Boolean> entry = (InformationEntry) informationEntry;
				this.fireEntry(msg.getHeader().getAsduAddress(), entry.getAddress(), entry.getValue());
			}
		}
	}

	public void process(BytestringPointInformationSequence msg) {
		if (!this.checkIgnore(msg)) {
			int i = msg.getStartAddress().getAddress();

			for(Iterator var4 = msg.getValues().iterator(); var4.hasNext(); ++i) {
				Value<Boolean> value = (Value)var4.next();
				this.fireEntry(msg.getHeader().getAsduAddress(), InformationObjectAddress.valueOf(i), value);
			}
		}
	}

	public abstract void interrogatedCorrectly();
}
