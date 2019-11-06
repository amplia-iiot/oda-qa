package tests.iec104.utils;

import es.amplia.oda.connector.iec104.types.BytestringPointInformationSequence;
import es.amplia.oda.connector.iec104.types.BytestringPointInformationSingle;
import org.eclipse.neoscada.protocol.iec60870.client.data.DataHandler;

public interface QADataHandler extends DataHandler {

	void process(BytestringPointInformationSingle var1);

	void process(BytestringPointInformationSequence var1);

	void interrogatedCorrectly();
}
