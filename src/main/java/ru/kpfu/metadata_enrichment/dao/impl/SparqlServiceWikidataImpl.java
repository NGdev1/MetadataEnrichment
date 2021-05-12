package ru.kpfu.metadata_enrichment.dao.impl;

import lombok.AllArgsConstructor;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;
import ru.kpfu.metadata_enrichment.dao.SparqlService;
import ru.kpfu.metadata_enrichment.model.AdditionalMetadata;
import ru.kpfu.metadata_enrichment.model.SemanticWebResource;
import ru.kpfu.metadata_enrichment.utils.PrefixesStorage;
import ru.kpfu.metadata_enrichment.utils.SparqlHttpClient;
import org.apache.jena.rdf.model.Literal;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class SparqlServiceWikidataImpl implements SparqlService {

    private final PrefixesStorage prefixesStorage;
    private final SparqlHttpClient sparqlHttpClient;

    @Override
    public List<SemanticWebResource> searchForHumans(String query) throws Exception {
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
        // System.out.println(queryEngineHTTP.getQueryString());

        List<SemanticWebResource> results = new ArrayList<>();
        ResultSet resultSet = queryEngineHTTP.execSelect();

        while (resultSet.hasNext()) {
            QuerySolution result = resultSet.next();

            SemanticWebResource resource = new SemanticWebResource();
            resource.setUri(result.get("object").toString());
            resource.setLabel(result.getLiteral("objectLabel").getLexicalForm());

            results.add(resource);
        }

        queryEngineHTTP.close();
        return results;
    }

    @Override
    public Optional<AdditionalMetadata> retrieveAdditionalMetadata(SemanticWebResource resource) throws Exception {
        QueryEngineHTTP queryEngineHTTP = new QueryEngineHTTP(sparqlHttpClient.getEndpointUrl(),
                PrefixesStorage.generatePrefixQueryString(prefixesStorage.getReplaceMap()) +
                        "SELECT ?mathNetId ?zbMATHAuthorID ?openLibraryID\n" +
                        "WHERE\n" +
                        "{\n" +
                        "    OPTIONAL {\n" +
                        "      <" + resource.getUri() + "> p:P4252 ?mathNetStatement .\n" +
                        "      ?mathNetStatement ps:P4252 ?mathNetId .\n" +
                        "    }\n" +
                        "  \n" +
                        "    OPTIONAL {\n" +
                        "      <" + resource.getUri() + "> p:P1556 ?zbMATHAuthorIDStatement .\n" +
                        "      ?zbMATHAuthorIDStatement ps:P1556 ?zbMATHAuthorID . \n" +
                        "    }\n" +
                        "  \n" +
                        "    OPTIONAL {\n" +
                        "      <" + resource.getUri() + "> p:P648 ?openLibraryIDStatement .\n" +
                        "      ?openLibraryIDStatement ps:P648 ?openLibraryID . \n" +
                        "    }\n" +
                        "} LIMIT 1"
        );
        // System.out.println(queryEngineHTTP.getQueryString());

        Optional<AdditionalMetadata> optionalResult;
        ResultSet resultSet = queryEngineHTTP.execSelect();

        if (resultSet.hasNext()) {
            QuerySolution result = resultSet.next();
            AdditionalMetadata additionalMetadata = new AdditionalMetadata();
            additionalMetadata.setLabel(resource.getLabel());
            additionalMetadata.setWikidataURI(resource.getUri());

            Literal mathNetId = result.getLiteral("mathNetId");
            if (mathNetId != null) {
                additionalMetadata.setMathNetId(mathNetId.toString());
            }

            Literal zbMATHAuthorID = result.getLiteral("zbMATHAuthorID");
            if (zbMATHAuthorID != null) {
                additionalMetadata.setZbMATHAuthorID(zbMATHAuthorID.toString());
            }

            Literal openLibraryID = result.getLiteral("openLibraryID");
            if (openLibraryID != null) {
                additionalMetadata.setOpenLibraryID(openLibraryID.toString());
            }

            optionalResult = Optional.of(additionalMetadata);
        } else {
            optionalResult = Optional.empty();
        }
        queryEngineHTTP.close();
        return optionalResult;
    }
}
