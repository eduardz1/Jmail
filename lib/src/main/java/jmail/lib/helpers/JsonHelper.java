package jmail.lib.helpers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonHelper {

  private static ObjectMapper getMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
    objectMapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
    objectMapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);
    return objectMapper;
  }

  public static <T> T fromJsonNode(JsonNode node, Class<T> clazz) throws JsonProcessingException {
    var mapper = getMapper();
    return mapper.treeToValue(node, clazz);
  }

  public static JsonNode toJsonNode(String json) throws JsonProcessingException {
    return getMapper().readTree(json);
  }

  public static <T> T fromJson(String json, Class<T> clazz) throws JsonProcessingException {
    ObjectMapper objectMapper = getMapper();
    return objectMapper.readValue(json, clazz);
  }

  public static <T> String toJson(T value) throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();

    return objectMapper.writeValueAsString(value);
  }
}
