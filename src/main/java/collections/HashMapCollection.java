package collections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Improved HashMap-based implementation of {@link Collection}.
 *
 * Each named collection is stored as a HashMap mapping location names to their
 * rankings. This avoids the linear search used in {@link ResortedArrayCollection}
 * and only sorts when getTopK is called, rather than re-sorting after every
 * add or update.
 *
 * Time complexity improvements over ResortedArrayCollection:
 *   - addLocation:      O(1) average  vs O(n log n) (no re-sort after every add)
 *   - removeLocation:   O(1) average  vs O(n)       (no linear search)
 *   - updateRanking:    O(1) average  vs O(n log n) (no re-sort after every update)
 *   - getTopK:          O(n log n)    vs O(1)       (sort only when needed)
 *   - mergeCollections: O(m)          vs O((m+n) log(m+n))
 */
public class HashMapCollection implements Collection {

    // Maps each collection name to a HashMap of location name -> ranking
    private final Map<String, Map<String, Double>> collections;

    public HashMapCollection() {
        this.collections = new HashMap<>();
    }

    @Override
    public void addLocation(String collection, String location, double ranking) {
        if (ranking < 0.0 || ranking > 10.0) {
            throw new IllegalArgumentException("Ranking must be between 0 and 10");
        }
        this.collections.putIfAbsent(collection, new HashMap<>());
        Map<String, Double> locationMap = this.collections.get(collection);
        if (locationMap.containsKey(location)) {
            throw new IllegalArgumentException("Collection already contains location: " + location);
        }
        // O(1) average — no sorting needed
        locationMap.put(location, ranking);
    }

    @Override
    public void removeLocation(String collection, String location) {
        if (!this.collections.containsKey(collection)) {
            throw new IllegalArgumentException("Collection does not exist: " + collection);
        }
        Map<String, Double> locationMap = this.collections.get(collection);
        if (!locationMap.containsKey(location)) {
            throw new IllegalArgumentException("Location does not exist: " + location);
        }
        // O(1) average — no linear search needed
        locationMap.remove(location);
    }

    @Override
    public boolean contains(String collection, String location) {
        if (!this.collections.containsKey(collection)) {
            throw new IllegalArgumentException("Collection does not exist: " + collection);
        }
        return this.collections.get(collection).containsKey(location);
    }

    @Override
    public int size(String collection) {
        if (!this.collections.containsKey(collection)) {
            return 0;
        }
        return this.collections.get(collection).size();
    }

    @Override
    public List<Location> getTopK(String collection, int k) {
        if (!this.collections.containsKey(collection)) {
            throw new IllegalArgumentException("Collection does not exist: " + collection);
        }
        // Build a list of Location objects and sort once on demand
        Map<String, Double> locationMap = this.collections.get(collection);
        List<Location> locationList = new ArrayList<>();
        for (Map.Entry<String, Double> entry : locationMap.entrySet()) {
            locationList.add(new Location(entry.getKey(), entry.getValue()));
        }
        Collections.sort(locationList, Collections.reverseOrder());
        int limit = Math.min(k, locationList.size());
        return new ArrayList<>(locationList.subList(0, limit));
    }

    @Override
    public void updateRanking(String collection, String location, double newRanking) {
        if (!this.collections.containsKey(collection)) {
            throw new IllegalArgumentException("Collection does not exist: " + collection);
        }
        Map<String, Double> locationMap = this.collections.get(collection);
        if (!locationMap.containsKey(location)) {
            throw new IllegalArgumentException("Location does not exist: " + location);
        }
        if (newRanking < 0.0 || newRanking > 10.0) {
            throw new IllegalArgumentException("Ranking must be between 0 and 10");
        }
        // O(1) average — just update the value, no re-sorting
        locationMap.put(location, newRanking);
    }

    @Override
    public void mergeCollections(String sourceCollection, String destinationCollection) {
        if (!this.collections.containsKey(sourceCollection) ||
                !this.collections.containsKey(destinationCollection)) {
            throw new IllegalArgumentException("Collection does not exist");
        }
        Map<String, Double> sourceMap = this.collections.get(sourceCollection);
        Map<String, Double> destMap = this.collections.get(destinationCollection);
        // Add each source location to destination only if not already present
        for (Map.Entry<String, Double> entry : sourceMap.entrySet()) {
            if (!destMap.containsKey(entry.getKey())) {
                destMap.put(entry.getKey(), entry.getValue());
            }
        }
        // Delete the source collection
        this.collections.remove(sourceCollection);
    }
}
