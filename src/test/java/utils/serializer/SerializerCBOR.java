package utils.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;

import java.io.IOException;

public class SerializerCBOR {
	private static final ObjectMapper MAPPER = new ObjectMapper(new CBORFactory());

	public static <T> T deserialize(byte[] value, Class<T> type) throws IOException {
		return MAPPER.readValue(value, type);
	}

	public static byte[] serialize(Object value) throws IOException {
		return MAPPER.writeValueAsBytes(value);
	}
}
