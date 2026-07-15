package collections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Simple array-based implementation of {@link Collection}.
 *
 * Each named collection is stored as a sorted ArrayList of {@link Location} objects.
 * The list is re-sorted in descending ranking order after every add or update,
 * maintaining the sorted invariant at the cost of O(n log n) per modification.
 * Location lookups use linear search, making them O(n).
 */
public class ResortedArrayCollection implements Collection {

    // Maps each collection name to its sorted list of locations
    private final Map<String, List<Location>> collections;

    public ResortedArrayCollection() {
        this.collections = new HashMap<>();
    }

    @Override
    public void addLocation(String collection, String location, double ranking) {
        if (this.collections.containsKey(collection) && contains(collection, location)) {
            throw new IllegalArgumentException("Collection already contains location");
        }
        if (ranking < 0.0 || ranking > 10.0) {
            throw new IllegalArgumentException("Ranking must be between 0 and 10");
        }
        if (!this.collections.containsKey(collection)) {
            this.collections.put(collection, new ArrayList<>());
        }
        List<Location> locationList = this.collections.get(collection);
        locationList.add(new Location(location, ranking));
        Collections.sort(locationList, Collections.reverseOrder());
    }

    @Override
    public void removeLocation(String collection, String location) {
        if (!this.collections.containsKey(collection) || !contains(collection, location)) {
            throw new IllegalArgumentException("Collection or location does not exist");
        }
        List<Location> locationList = this.collections.get(collection);
        Iterator<Location> it = locationList.iterator();
        while (it.hasNext()) {
            if (it.next().name.equals(location)) {
                it.remove();
                return;
            }
        }
    }

    @Override
    public boolean contains(String collection, String location) {
        if (!this.collections.containsKey(collection)) {
            throw new IllegalArgumentException("Collection does not exist: " + collection);
        }
        for (Location l : this.collections.get(collection)) {
            if (l.name.equals(location)) {
                return true;
            }
        }
        return false;
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
        List<Location> locationList = this.collections.get(collection);
        int limit = Math.min(k, locationList.size());
        return new ArrayList<>(locationList.subList(0, limit));
    }

    @Override
    public void updateRanking(String collection, String location, double newRanking) {
        if (!this.collections.containsKey(collection)) {
            throw new IllegalArgumentException("Collection does not exist: " + collection);
        }
        if (!contains(collection, location)) {
            throw new IllegalArgumentException("Location does not exist: " + location);
        }
        if (newRanking < 0.0 || newRanking > 10.0) {
            throw new IllegalArgumentException("Ranking must be between 0 and 10");
        }
        List<Location> locationList = this.collections.get(collection);
        for (Location l : locationList) {
            if (l.name.equals(location)) {
                l.ranking = newRanking;
                break;
            }
        }
        Collections.sort(locationList, Collections.reverseOrder());
    }

    @Override
    public void mergeCollections(String sourceCollection, String destinationCollection) {
        if (!this.collections.containsKey(sourceCollection) ||
                !this.collections.containsKey(destinationCollection)) {
            throw new IllegalArgumentException("Collection does not exist");
        }
        List<Location> sourceList = this.collections.get(sourceCollection);
        for (Location l : sourceList) {
            if (!contains(destinationCollection, l.name)) {
                this.collections.get(destinationCollection).add(l);
            }
        }
        Collections.sort(this.collections.get(destinationCollection), Collections.reverseOrder());
        this.collections.remove(sourceCollection);
    }
}
