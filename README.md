# Husky Maps

A full-stack mapping application built in Java, implementing the core data structures and algorithms that power location search, autocomplete, and turn-by-turn routing over real Seattle map data.

Built on top of the [Husky Maps](https://huskymaps.kevinl.info/) educational framework created by [Kevin Lin](https://kevinl.info/) for the University of Washington's CSE 373 (Data Structures & Algorithms) course. The routing engine, search structures, and optimized collection feature below are my own implementation.

## What it does

- **Search & autocomplete** over real location data, backed by a custom ternary search tree
- **Turn-by-turn routing** between two points using A* and Dijkstra's shortest-path algorithms over a real-world road network graph
- **Saved locations**, backed by a custom-optimized collection structure

## Technical highlights

- **Routing:** Implemented A* and Dijkstra's algorithms with an indexed binary heap priority queue, giving O(log n) insert/decrease-key via a hashmap-backed index, for efficient shortest-path routing over the Seattle road network.
- **Autocomplete:** Designed a ternary search tree for prefix search, reducing lookups from a linear scan to O(k + m) (k = prefix length, m = number of matches), benchmarked against binary search and sequential baselines.
- **Optimized Collections:** Redesigned a "saved locations" feature from a re-sorted array to a hashmap-backed structure, cutting average-case insert/update operations from O(n log n) to O(1).
- **Testing:** Verified correctness with property-based testing (jqwik), asserting behavioral equivalence between simple and optimized implementations across randomized inputs, with 85%+ test coverage.

## Running it locally

**Requirements:** [JDK 17+](https://adoptium.net/), Git

```bash
git clone https://github.com/randyxia07/husky-maps.git
cd husky-maps
./gradlew build
./gradlew test
java -jar build/libs/huskymaps-all.jar
```

Then open `http://localhost:8080/` in your browser. Search, autocomplete, and routing all work out of the box. Map tile images require a free MapBox token (see below) — without one, the app still runs, just without the visual map background.

### Optional: Load map images with MapBox

1. [Sign up for a free MapBox account](https://account.mapbox.com/auth/signup/) and grab an access token from your [account dashboard](https://account.mapbox.com/access-tokens/).
2. Run the app with the token set as an environment variable:

```bash
# macOS/Linux
TOKEN="your-token-here" java -jar build/libs/huskymaps-all.jar

# Windows PowerShell
$env:TOKEN="your-token-here"
java -jar build/libs/huskymaps-all.jar
```

MapBox's free tier covers up to 50,000 requests/month, well beyond what local use or a personal demo needs.

## Tech stack

Java 17 · Gradle · Javalin (web server) · JUnit & jqwik (testing) · MapBox Static Images API

## Original course setup instructions

For UW CSE 373 students following the original course setup (GitLab, SSH keys, VS Code configuration), see [COURSE_SETUP.md](COURSE_SETUP.md).

## License

This project builds on the Husky Maps course framework by Kevin Lin, distributed under the MIT License (see [LICENSE](LICENSE)).
