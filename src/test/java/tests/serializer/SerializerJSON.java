package tests.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class SerializerJSON {
	private static final ObjectMapper MAPPER = new ObjectMapper();

	public static  <T> T deserialize(byte[] value, Class<T> type) throws IOException {
		return MAPPER.readValue(value, type);
	}

	public static byte[] serialize(Object value) throws IOException {
		return MAPPER.writeValueAsBytes(value);
	}
}
