import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

/**
 * Display the most commonly-reported WCAG recommendations.
 */
public class ReportAnalyzer {
    public static void main(String[] args) throws IOException {
        File inputFile = new File("data/wcag.tsv");
        Map<String, String> wcagDefinitions = new LinkedHashMap<>();
        try (Scanner scanner = new Scanner(inputFile)) {
            while (scanner.hasNextLine()) {
                String[] line = scanner.nextLine().split("\t", 2);
                String index = "wcag" + line[0].replace(".", "");
                String title = line[1];
                wcagDefinitions.put(index, title);
            }
        }
        Pattern re = Pattern.compile("wcag\\d{3,4}");
        List<String> wcagTags = Files.walk(Paths.get("data/reports"))
                .map(path -> {
                    try {
                        return Files.readString(path);
                    } catch (IOException e) {
                        return "";
                    }
                })
                .flatMap(contents -> re.matcher(contents).results())
                .map(MatchResult::group)
                .toList();

        // Count frequency of each WCAG tag
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (String tag : wcagTags) {
            counts.put(tag, counts.getOrDefault(tag, 0) + 1);
        }

        // Use a MinPQ with negated counts so the most frequent tag has the lowest priority
        minpq.MinPQ<String> pq = new minpq.OptimizedHeapMinPQ<>();
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            pq.add(entry.getKey(), -entry.getValue());
        }

        // Display most-common to least-common
        while (!pq.isEmpty()) {
            String tag = pq.removeMin();
            int count = counts.get(tag);
            String title = wcagDefinitions.getOrDefault(tag, "(unknown)");
            System.out.println(count + "\t" + tag + "\t" + title);
        }
    }
}