package ru.kpfu.metadata_enrichment;

import ru.kpfu.metadata_enrichment.config.DIContainer;
import ru.kpfu.metadata_enrichment.model.SemanticWebResource;
import ru.kpfu.metadata_enrichment.services.PersonService;

import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        PersonService personService = DIContainer.instance.getPersonService();
        Optional<SemanticWebResource> optionalResource = personService.searchForHuman("А. Н. Хованский");

        if(optionalResource.isPresent() == false) {
            System.out.println("Ресурс не найден");
            return;
        }
        SemanticWebResource resource = optionalResource.get();
        System.out.println(resource.getLabel() + " " + resource.getUri());
    }
}
