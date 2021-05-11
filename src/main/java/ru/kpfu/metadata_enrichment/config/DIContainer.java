package ru.kpfu.metadata_enrichment.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import ru.kpfu.metadata_enrichment.utils.PrefixesStorage;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

@Getter
public class DIContainer {
    public static DIContainer instance = new DIContainer();

    private final PrefixesStorage prefixesStorage;
    private final ObjectMapper mapper = new ObjectMapper();

    private DIContainer() {
        prefixesStorage = getPrefixesStorage();
    }

    private PrefixesStorage getPrefixesStorage() {
        TypeReference<LinkedHashMap<String, String>> typeReference = new TypeReference<LinkedHashMap<String, String>>(){};
        Map<String, String> prefixes;
        try {
            InputStream resource = new FileInputStream("prefixes.json");
            JsonNode tree = mapper.readTree(resource);
            prefixes = mapper.readValue(tree.path("prefixes").toString(), typeReference);
        } catch (IOException e){
            System.out.println("Unable to read config: " + e.getMessage());
            return null;
        }

        return new PrefixesStorage(prefixes);
    }

}
