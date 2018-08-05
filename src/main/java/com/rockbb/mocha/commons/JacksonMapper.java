package com.rockbb.mocha.commons;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class JacksonMapper {
	private static final ObjectMapper mapper = new ObjectMapper();
	private JacksonMapper() {}

	public static ObjectMapper getInstance() {
		return mapper;
	}

	public static void writeObject(PrintWriter writer, Object data) {
		try {
			mapper.writeValue(writer, data);
		} catch (JsonGenerationException e) {
			// Do nothing
		} catch (IOException e) {
			// Do nothing
		}
	}

	public static String compressStringList(List<String> objects) {
		String value = "";
		ObjectMapper mapper = JacksonMapper.getInstance();
		try {
			value = mapper.writeValueAsString(objects);
		} catch (JsonGenerationException e) {
			// Do nothing
		} catch (IOException e) {
			// Do nothing
		}
		return value;
	}

	public static List<String> extractList(String jsonString) {
		List<String> list= new ArrayList<String>();

		ObjectMapper mapper = JacksonMapper.getInstance();
		try {
			list = mapper.readValue(jsonString, ArrayList.class);
		} catch (JsonParseException e) {
			// Do nothing
		} catch (IOException e) {
			// Do nothing
		}

		return list;
	}
}
