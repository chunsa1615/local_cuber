package kr.co.cntt.scc.util;

import java.io.IOException;
import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.CollectionType;

public final class JSONUtils {
    private final ObjectMapper mapper;
    
    private JSONUtils() {
        mapper = new ObjectMapper();
        //mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(MapperFeature.AUTO_DETECT_GETTERS, true);
        mapper.configure(MapperFeature.AUTO_DETECT_IS_GETTERS, false);
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        mapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false);
        mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
    }
    
    public static JSONUtils getInstance() {
        return new JSONUtils();
    }
    
    private static ObjectMapper getMapper() {
        return getInstance().mapper;
    }
    
    public static String toJson(Object object) {
        try {
            return getMapper().writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public static <T> T fromJson(String jsonStr, Class<T> cls) {
            try {
                return getMapper().readValue(jsonStr, cls);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
    }
    
    public static <T> T fromJson(String jsonStr, TypeReference<T> typeReference) {
        try {
            return getMapper().readValue(jsonStr, typeReference);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public static JsonNode fromJson(String json) throws Exception {
        try {
            return getMapper().readTree(json);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    public static <T extends Collection> T fromJson(String jsonStr, CollectionType collectionType) {
        try {
            return getMapper().readValue(jsonStr, collectionType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public static String toPrettyJson(String json) {
        Object jsonObject = JSONUtils.fromJson(json, Object.class);
        try {
            return getMapper().writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "";
    }
    
    public static Object JsonToObjWithoutRoot(String json, Class<?> targetClass, String rootName) {
        
        Object output = null;
        ObjectMapper mapper = new ObjectMapper();
        
        @SuppressWarnings("deprecation")
        ObjectReader reader = mapper.reader(targetClass).withRootName(rootName);
        
        try {
            output = reader.readValue(json);
        } catch (Exception e) {
            output = null;
        }
        
        return output;
    }
    
}