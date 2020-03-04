package es.amplia.oda.connector.dnp3.utils;

import com.automatak.dnp3.LogEntry;
import com.automatak.dnp3.LogHandler;
import com.automatak.dnp3.LogLevels;

public class DNP3LogHandler implements LogHandler {
	@Override
	public void log(LogEntry entry) {
		switch (entry.level) {
			case LogLevels.ERROR:
				System.out.println("LOG ERROR: " + entry.message);
				break;
			case LogLevels.WARNING:
				System.out.println("LOG WARN: " + entry.message);
				break;
			case LogLevels.INFO:
				System.out.println("LOG INFO: " + entry.message);
				break;
			case LogLevels.DEBUG:
			default:
				System.out.println("LOG DEBUG: " + entry.message);
		}
	}
}
