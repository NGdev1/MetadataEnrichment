package ru.kpfu.metadata_enrichment.services.impl;

import lombok.AllArgsConstructor;
import ru.kpfu.metadata_enrichment.dao.SparqlService;
import ru.kpfu.metadata_enrichment.model.SemanticWebResource;
import ru.kpfu.metadata_enrichment.services.PersonService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
public class PersonServiceImpl implements PersonService {

    private final SparqlService sparqlService;

    @Override
    public Optional<SemanticWebResource> searchForHuman(String fullName) {
        List<String> splitted = Arrays.asList(fullName.split("[.\\s]"));
        if(splitted.size() == 0) {
            return Optional.empty();
        }
        String familyName = splitted.get(splitted.size() - 1);
        List<SemanticWebResource> resources = sparqlService.searchForHumans(familyName);

        for(SemanticWebResource resource : resources) {
            if(checkFullName(fullName, resource.getLabel())) {
                return Optional.of(resource);
            }
        }
        return Optional.empty();
    }

    private Boolean checkFullName(String name1, String name2) {
        // 1
        name1 = name1.replace("ё", "е");
        name2 = name2.replace("ё", "е");

        List<String> name1Splitted = Arrays.stream(name1.split("[^a-zA-Zа-яА-Я]"))
                .filter(item -> item.isEmpty() == false)
                .collect(Collectors.toList());
        List<String> name2Splitted = Arrays.stream(name2.split("[^a-zA-Zа-яА-Я]"))
                .filter(item -> item.isEmpty() == false)
                .collect(Collectors.toList());

        // 2
        List<String> name1Filtered = name1Splitted.stream()
                .filter(item -> name2Splitted.contains(item) == false)
                .collect(Collectors.toList());
        List<String> name2Filtered = name2Splitted.stream()
                .filter(item -> name1Splitted.contains(item) == false)
                .collect(Collectors.toList());

        // 3
        boolean result = true;
        for(int i = 0; i < name1Filtered.size() && i < name2Filtered.size(); i++) {
            String name1Item = name1Filtered.get(i);
            String name2Item = name2Filtered.get(i);

            if(name1Item.isEmpty() || name2Item.isEmpty()) {
                result = false;
                continue;
            }
            if (name1Item.charAt(0) != name2Item.charAt(0)) {
                result = false;
            }
        }
        return result;
    }
}
