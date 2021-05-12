package ru.kpfu.metadata_enrichment.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CsvUtils {
    // Разделитель csv файла
    private static final String DELIMITER = ";";

    public static List<List<String>> readCsv(String fileName) throws Exception {
            List<List<String>> results = new ArrayList<>();
            String line;
            BufferedReader fileReader = new BufferedReader(new FileReader(fileName));
            while ((line = fileReader.readLine()) != null) {
                String[] tokens = line.split("[" + DELIMITER + "]");
                List<String> row = new ArrayList<>(Arrays.asList(tokens));
                if (line.endsWith(DELIMITER)) {
                    row.add("");
                }
                results.add(row);
            }
            fileReader.close();
            return results;
    }

    public static void writeCsv(String fileName, List<List<String>> rows) throws Exception {
        FileWriter csvWriter = new FileWriter(fileName);
        for (List<String> rowData : rows) {
            csvWriter.append(String.join(DELIMITER, rowData));
            csvWriter.append("\n");
        }
        csvWriter.flush();
        csvWriter.close();
    }
}
