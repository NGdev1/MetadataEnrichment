package ru.kpfu.metadata_enrichment;

import ru.kpfu.metadata_enrichment.config.DIContainer;
import ru.kpfu.metadata_enrichment.model.AdditionalMetadata;
import ru.kpfu.metadata_enrichment.model.SemanticWebResource;
import ru.kpfu.metadata_enrichment.services.PersonService;
import ru.kpfu.metadata_enrichment.utils.CsvUtils;

import java.util.List;
import java.util.Optional;

public class Main {
    private final static int authorRowIndex = 7;

    public static void main(String[] args) throws Exception {
        PersonService personService = DIContainer.instance.getPersonService();

        List<List<String>> allRows = CsvUtils.readCsv("input.csv");

        for(int i = 0; i < allRows.size(); i++){
            List<String> row = allRows.get(i);

            // Пропуск заголовка таблицы
            if (i == 0) {
                row.add("WikidataURI");
                row.add("MathNetId");
                row.add("ZbMATHAuthorID");
                row.add("OpenLibraryID");
                allRows.set(i, row);
                continue;
            }

            String authorFullName = row.get(authorRowIndex);
            Optional<SemanticWebResource> optionalResource = personService.searchForHuman(authorFullName);

            if(optionalResource.isPresent() == false) {
                System.out.println(authorFullName + ": Ресурс не найден");
                row.add(""); row.add(""); row.add(""); row.add("");
                allRows.set(i, row);
                continue;
            }
            SemanticWebResource resource = optionalResource.get();
            Optional<AdditionalMetadata> optionalMetadata = personService.retrieveAdditionalMetadata(resource);
            if(optionalMetadata.isPresent() == false) {
                System.out.println(authorFullName + ": Метаданные не найдены");
                row.add(""); row.add(""); row.add(""); row.add("");
                allRows.set(i, row);
                continue;
            }

            AdditionalMetadata metadata = optionalMetadata.get();
            row.add(metadata.getWikidataURI());
            row.add(metadata.getMathNetId());
            row.add(metadata.getZbMATHAuthorID());
            row.add(metadata.getOpenLibraryID());

            System.out.println(metadata.getLabel() + " WikidataURI: " + metadata.getWikidataURI() + " MathNetId:" + metadata.getMathNetId() +
                    " ZbMATHAuthorID:" + metadata.getZbMATHAuthorID() + " OpenLibraryID:" + metadata.getOpenLibraryID());
            allRows.set(i, row);
        }

        CsvUtils.writeCsv("output.csv", allRows);
    }
}
