package jmail.lib.helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

// FIXME: serve davvero?

public class JsonHelper {
  private static final ObjectMapper mapper = new ObjectMapper();

  public static <T> T fromJsonNode(JsonNode node, Class<T> clazz) throws JsonProcessingException {
    return mapper.treeToValue(node, clazz);
  }

  public static JsonNode toJsonNode(String json) throws JsonProcessingException {
    return mapper.readTree(json);
  }

  public static <T> T fromJson(String json, Class<T> clazz) throws JsonProcessingException {
    return mapper.readValue(json, clazz);
  }

  public static <T> String toJson(T value) throws JsonProcessingException {
    return mapper.writeValueAsString(value);
  }
}
