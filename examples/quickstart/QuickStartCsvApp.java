import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

public class QuickStartCsvApp {

    public static void main(String[] args) throws IOException {
        final Path input = Paths.get("examples", "quickstart", "input", "orders.csv");
        final Path outputDir = Paths.get("examples", "quickstart", "output");
        final Path output = outputDir.resolve("paid-orders-summary.csv");

        Files.createDirectories(outputDir);

        BigDecimal paidTotal = BigDecimal.ZERO;
        int paidCount = 0;

        final CSVFormat inputFormat = CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .setTrim(true)
                .build();

        try (CSVParser parser = CSVParser.parse(input, java.nio.charset.StandardCharsets.UTF_8, inputFormat)) {
            for (CSVRecord record : parser) {
                if ("PAID".equalsIgnoreCase(record.get("status"))) {
                    paidCount++;
                    paidTotal = paidTotal.add(new BigDecimal(record.get("amount")));
                }
            }
        }

        final CSVFormat outputFormat = CSVFormat.DEFAULT.builder()
            .setHeader("metric", "value")
                .build();

        try (CSVPrinter printer = new CSVPrinter(Files.newBufferedWriter(output), outputFormat)) {
            printer.printRecord("paid_count", paidCount);
            printer.printRecord("paid_total", paidTotal);
        }

        System.out.println("Input : " + input.toAbsolutePath());
        System.out.println("Output: " + output.toAbsolutePath());
        System.out.println("paid_count=" + paidCount + ", paid_total=" + paidTotal);
    }
}
