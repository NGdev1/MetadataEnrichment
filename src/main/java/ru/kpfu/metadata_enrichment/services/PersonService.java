package ru.kpfu.metadata_enrichment.services;

import ru.kpfu.metadata_enrichment.model.SemanticWebResource;

import java.util.List;
import java.util.Optional;

public interface PersonService {
    Optional<SemanticWebResource> searchForHuman(String fullName);
}
