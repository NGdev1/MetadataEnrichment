package ru.kpfu.metadata_enrichment.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import ru.kpfu.metadata_enrichment.dao.SparqlService;
import ru.kpfu.metadata_enrichment.dao.impl.SparqlServiceWikidataImpl;
import ru.kpfu.metadata_enrichment.services.PersonService;
import ru.kpfu.metadata_enrichment.services.impl.PersonServiceImpl;
import ru.kpfu.metadata_enrichment.utils.PrefixesStorage;
import ru.kpfu.metadata_enrichment.utils.SparqlHttpClient;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

@Getter
public class DIContainer {
    public static DIContainer instance = new DIContainer();

    private final ObjectMapper mapper = new ObjectMapper();
    private final PrefixesStorage prefixesStorage = getPrefixesStorage();
    private final SparqlHttpClient sparqlHttpClient = getSparqlHttpClient();
    private final SparqlService sparqlService = new SparqlServiceWikidataImpl(prefixesStorage, sparqlHttpClient);
    private final PersonService personService = new PersonServiceImpl(sparqlService);

    private DIContainer() {}

    private PrefixesStorage getPrefixesStorage() {
        TypeReference<LinkedHashMap<String, String>> typeReference = new TypeReference<LinkedHashMap<String, String>>(){};
        Map<String, String> prefixes;
        try {
            InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("configprops.json");
            JsonNode tree = mapper.readTree(resourceAsStream);
            prefixes = mapper.readValue(tree.path("prefixes").toString(), typeReference);
        } catch (Exception e) {
            System.out.println("Unable to read config: " + e.getMessage());
            prefixes = new LinkedHashMap<>();
        }
        return new PrefixesStorage(prefixes);
    }

    private SparqlHttpClient getSparqlHttpClient() {
        String endpointUrl = "";
        try {
            InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("configprops.json");
            JsonNode tree = mapper.readTree(resourceAsStream);
            endpointUrl = tree.path("endpointUrl").asText();
        } catch (IOException e){
            System.out.println("Unable to read config: " + e.getMessage());
        }
        return new SparqlHttpClient(endpointUrl);
    }

}
