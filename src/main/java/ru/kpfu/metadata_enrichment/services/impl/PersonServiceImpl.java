package ru.kpfu.metadata_enrichment.services.impl;

import lombok.AllArgsConstructor;
import ru.kpfu.metadata_enrichment.dao.SparqlService;
import ru.kpfu.metadata_enrichment.model.AdditionalMetadata;
import ru.kpfu.metadata_enrichment.model.SemanticWebResource;
import ru.kpfu.metadata_enrichment.services.PersonService;
import ru.kpfu.metadata_enrichment.utils.FullNameUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
public class PersonServiceImpl implements PersonService {

    private final SparqlService sparqlService;

    @Override
    public Optional<SemanticWebResource> searchForHuman(String fullName) throws Exception {
        List<String> splitted = Arrays.asList(fullName.split("[.\\s]"));
        if(splitted.size() == 0) {
            return Optional.empty();
        }
        String familyName = splitted.get(splitted.size() - 1);
        List<SemanticWebResource> resources = sparqlService.searchForHumans(familyName);

        for(SemanticWebResource resource : resources) {
            if(FullNameUtils.checkFullName(fullName, resource.getLabel())) {
                return Optional.of(resource);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<AdditionalMetadata> retrieveAdditionalMetadata(SemanticWebResource resource) throws Exception {
        return sparqlService.retrieveAdditionalMetadata(resource);
    }
}
