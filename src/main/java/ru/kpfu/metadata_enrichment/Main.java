package ru.kpfu.metadata_enrichment;

import ru.kpfu.metadata_enrichment.config.DIContainer;
import ru.kpfu.metadata_enrichment.model.AdditionalMetadata;
import ru.kpfu.metadata_enrichment.model.SemanticWebResource;
import ru.kpfu.metadata_enrichment.services.PersonService;

import java.util.Optional;

public class Main {
    public static void main(String[] args) throws Exception {
        PersonService personService = DIContainer.instance.getPersonService();
        Optional<SemanticWebResource> optionalResource = personService.searchForHuman("Н. Г. Чеботарев");

        if(optionalResource.isPresent() == false) {
            System.out.println("Ресурс не найден");
            return;
        }
        SemanticWebResource resource = optionalResource.get();
        Optional<AdditionalMetadata> optionalMetadata = personService.retrieveAdditionalMetadata(resource);
        if(optionalMetadata.isPresent() == false) {
            System.out.println("Метаданные не найдены");
            return;
        }

        AdditionalMetadata metadata = optionalMetadata.get();
        System.out.println(metadata.getLabel() + " " + metadata.getWikidataURI() + " MathNetId:" + metadata.getMathNetId() +
                " ZbMATHAuthorID:" + metadata.getZbMATHAuthorID() + " OpenLibraryID:" + metadata.getOpenLibraryID());
    }
}
