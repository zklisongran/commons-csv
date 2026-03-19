import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

public class InventorySummaryApp {

    public static void main(String[] args) throws IOException {
        final Path input = Paths.get("examples", "windows11-demo", "input", "inventory.csv");
        final Path outputDir = Paths.get("examples", "windows11-demo", "output");
        final Path output = outputDir.resolve("inventory-summary.csv");

        Files.createDirectories(outputDir);

        int activeCount = 0;
        BigDecimal activeStockValue = BigDecimal.ZERO;

        final CSVFormat inputFormat = CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .setTrim(true)
                .build();

        try (CSVParser parser = CSVParser.parse(input, StandardCharsets.UTF_8, inputFormat)) {
            for (CSVRecord record : parser) {
                if ("ACTIVE".equalsIgnoreCase(record.get("status"))) {
                    activeCount++;
                    final BigDecimal qty = new BigDecimal(record.get("qty"));
                    final BigDecimal unitPrice = new BigDecimal(record.get("unit_price"));
                    activeStockValue = activeStockValue.add(qty.multiply(unitPrice));
                }
            }
        }

        final CSVFormat outputFormat = CSVFormat.DEFAULT.builder()
                .setHeader("metric", "value")
                .build();

        try (CSVPrinter printer = new CSVPrinter(Files.newBufferedWriter(output), outputFormat)) {
            printer.printRecord("active_item_count", activeCount);
            printer.printRecord("active_stock_value", activeStockValue);
        }

        System.out.println("Input : " + input.toAbsolutePath());
        System.out.println("Output: " + output.toAbsolutePath());
        System.out.println("active_item_count=" + activeCount + ", active_stock_value=" + activeStockValue);
    }
}
