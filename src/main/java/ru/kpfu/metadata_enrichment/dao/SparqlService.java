package ru.kpfu.metadata_enrichment.dao;

import ru.kpfu.metadata_enrichment.model.AdditionalMetadata;
import ru.kpfu.metadata_enrichment.model.SemanticWebResource;

import java.util.List;
import java.util.Optional;

public interface SparqlService {
    List<SemanticWebResource> searchForHumans(String query) throws Exception;
    Optional<AdditionalMetadata> retrieveAdditionalMetadata(SemanticWebResource resource) throws Exception;
}
