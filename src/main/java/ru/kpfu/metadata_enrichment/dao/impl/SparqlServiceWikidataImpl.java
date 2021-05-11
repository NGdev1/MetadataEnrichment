package ru.kpfu.metadata_enrichment.dao.impl;

import lombok.AllArgsConstructor;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;
import ru.kpfu.metadata_enrichment.dao.SparqlService;
import ru.kpfu.metadata_enrichment.model.SemanticWebResource;
import ru.kpfu.metadata_enrichment.utils.PrefixesStorage;
import ru.kpfu.metadata_enrichment.utils.SparqlHttpClient;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class SparqlServiceWikidataImpl implements SparqlService {

    private final PrefixesStorage prefixesStorage;
    private final SparqlHttpClient sparqlHttpClient;

    @Override
    public List<SemanticWebResource> searchForHumans(String query) {
        QueryEngineHTTP queryEngineHTTP = new QueryEngineHTTP(sparqlHttpClient.getEndpointUrl(),
                PrefixesStorage.generatePrefixQueryString(prefixesStorage.getReplaceMap()) +
                        "SELECT DISTINCT ?object ?objectLabel\n" +
                        "WHERE\n" +
                        "{\n" +
                        "    ?object wdt:P31 wd:Q5 .\n" +
                        "    SERVICE wikibase:mwapi {\n" +
                        "       bd:serviceParam wikibase:endpoint \"www.wikidata.org\";\n" +
                        "                      wikibase:api \"EntitySearch\";\n" +
                        "                      mwapi:search \"" + query + "\";\n" +
                        "                      mwapi:language \"ru\".\n" +
                        "       ?object wikibase:apiOutputItem mwapi:item.\n" +
                        "    }\n" +
                        "    SERVICE wikibase:label {bd:serviceParam wikibase:language \"ru\".}\n" +
                        "} LIMIT 100"
        );

        System.out.println(queryEngineHTTP.getQueryString());

        List<SemanticWebResource> results = new ArrayList<>();
        try {
            ResultSet resultSet = queryEngineHTTP.execSelect();

            while (resultSet.hasNext()) {
                QuerySolution result = resultSet.next();

                SemanticWebResource currentTriple = new SemanticWebResource();
                currentTriple.setUri(result.get("object").toString());
                currentTriple.setLabel(result.getLiteral("objectLabel").getLexicalForm());

                results.add(currentTriple);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        queryEngineHTTP.close();
        return results;
    }
}
