package ru.kpfu.metadata_enrichment.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdditionalMetadata {
    String wikidataURI;
    String label;
    String mathNetId;
    String zbMATHAuthorID;
    String openLibraryID;
}
