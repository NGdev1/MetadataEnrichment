package ru.kpfu.metadata_enrichment.dao;

import ru.kpfu.metadata_enrichment.model.SemanticWebResource;

import java.util.List;

public interface SparqlService {
    List<SemanticWebResource> searchForHumans(String query);
}
