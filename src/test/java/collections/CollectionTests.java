package collections;

import net.jqwik.api.*;
import net.jqwik.api.constraints.DoubleRange;
import net.jqwik.api.constraints.Size;

import java.util.List;

import collections.Collection.Location;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Abstract test class for {@link Collection} implementations.
 * Subclasses provide a specific implementation via {@link #createCollection()}.
 */
public abstract class CollectionTests {

    private Collection collection;

    public abstract Collection createCollection();

    @Provide
    Arbitrary<Double> rankings() {
        return Arbitraries.doubles().between(0.0, 10.0).between(0.0, 10.0);
    }

    // --- basic working tests ---

    @Example
    void addAndTopK1() {
        collection = createCollection();
        collection.addLocation("coffee", "Starbucks", 8.0);
        collection.addLocation("coffee", "Blue Bottle", 9.5);
        assertEquals("Blue Bottle", collection.getTopK("coffee", 1).get(0).name);
    }

    @Property
    void getTopK1AlwaysReturnsHighestRanking(
            @ForAll @Size(min = 1, max = 20) List<@From("rankings") Double> rankings) {
        collection = createCollection();
        for (int i = 0; i < rankings.size(); i++) {
            collection.addLocation("test", "location " + i, rankings.get(i));
        }
        double max = 0;
        for (double r : rankings) {
            max = Math.max(max, r);
        }
        assertEquals(max, collection.getTopK("test", 1).get(0).ranking);
    }

    @Property
    void getTopKIsOrdered(
            @ForAll @Size(min = 1, max = 20) List<@From("rankings") Double> rankings) {
        collection = createCollection();
        for (int i = 0; i < rankings.size(); i++) {
            collection.addLocation("test", "location " + i, rankings.get(i));
        }
        List<Location> topK = collection.getTopK("test", rankings.size());
        for (int i = 0; i < topK.size() - 1; i++) {
            assertTrue(topK.get(i).ranking >= topK.get(i + 1).ranking);
        }
    }

    @Example
    void removeTopUpdatesNextTop() {
        collection = createCollection();
        collection.addLocation("coffee", "A", 9.0);
        collection.addLocation("coffee", "B", 7.0);
        collection.addLocation("coffee", "C", 5.0);
        collection.removeLocation("coffee", "A");
        assertEquals("B", collection.getTopK("coffee", 1).get(0).name);
    }

    @Property
    void removeDecreasesSize(
            @ForAll @Size(min = 1, max = 20) List<@From("rankings") Double> rankings) {
        collection = createCollection();
        for (int i = 0; i < rankings.size(); i++) {
            collection.addLocation("test", "location " + i, rankings.get(i));
        }
        collection.removeLocation("test", "location 0");
        assertEquals(rankings.size() - 1, collection.size("test"));
    }

    @Property
    void removeDoesNotContain(
            @ForAll @Size(min = 1, max = 20) List<@From("rankings") Double> rankings) {
        collection = createCollection();
        for (int i = 0; i < rankings.size(); i++) {
            collection.addLocation("test", "location " + i, rankings.get(i));
        }
        collection.removeLocation("test", "location 0");
        assertFalse(collection.contains("test", "location 0"));
    }

    @Example
    void updateChangesRanking() {
        collection = createCollection();
        collection.addLocation("coffee", "Starbucks", 5.0);
        collection.updateRanking("coffee", "Starbucks", 9.0);
        assertEquals(9.0, collection.getTopK("coffee", 1).get(0).ranking);
    }

    @Example
    void updateChangesOrder() {
        collection = createCollection();
        collection.addLocation("coffee", "A", 9.0);
        collection.addLocation("coffee", "B", 5.0);
        collection.updateRanking("coffee", "A", 1.0);
        assertEquals("B", collection.getTopK("coffee", 1).get(0).name);
    }

    @Property
    void updateDoesNotChangeSizeOrContains(
            @ForAll @Size(min = 1, max = 20) List<@From("rankings") Double> rankings,
            @ForAll @DoubleRange(min = 0, max = 10) double newRanking) {
        collection = createCollection();
        for (int i = 0; i < rankings.size(); i++) {
            collection.addLocation("test", "location " + i, rankings.get(i));
        }
        collection.updateRanking("test", "location 0", newRanking);
        assertEquals(rankings.size(), collection.size("test"));
        assertTrue(collection.contains("test", "location 0"));
    }

    @Property
    void updateTopKStillOrdered(
            @ForAll @Size(min = 2, max = 20) List<@From("rankings") Double> rankings,
            @ForAll @DoubleRange(min = 0, max = 10) double newRanking) {
        collection = createCollection();
        for (int i = 0; i < rankings.size(); i++) {
            collection.addLocation("test", "location " + i, rankings.get(i));
        }
        collection.updateRanking("test", "location 0", newRanking);
        List<Location> topK = collection.getTopK("test", rankings.size());
        for (int i = 0; i < topK.size() - 1; i++) {
            assertTrue(topK.get(i).ranking >= topK.get(i + 1).ranking);
        }
    }

    @Property
    void mergeDestinationContainsAllUniqueLocations(
            @ForAll @Size(min = 1, max = 10) List<@From("rankings") Double> sourceRankings,
            @ForAll @Size(min = 1, max = 10) List<@From("rankings") Double> destRankings) {
        collection = createCollection();
        for (int i = 0; i < sourceRankings.size(); i++) {
            collection.addLocation("source", "s" + i, sourceRankings.get(i));
        }
        for (int i = 0; i < destRankings.size(); i++) {
            collection.addLocation("dest", "d" + i, destRankings.get(i));
        }
        collection.mergeCollections("source", "dest");
        for (int i = 0; i < destRankings.size(); i++) {
            assertTrue(collection.contains("dest", "d" + i));
        }
        for (int i = 0; i < sourceRankings.size(); i++) {
            assertTrue(collection.contains("dest", "s" + i));
        }
    }

    @Property
    void mergeResultIsOrdered(
            @ForAll @Size(min = 1, max = 10) List<@From("rankings") Double> sourceRankings,
            @ForAll @Size(min = 1, max = 10) List<@From("rankings") Double> destRankings) {
        collection = createCollection();
        for (int i = 0; i < sourceRankings.size(); i++) {
            collection.addLocation("source", "s" + i, sourceRankings.get(i));
        }
        for (int i = 0; i < destRankings.size(); i++) {
            collection.addLocation("dest", "d" + i, destRankings.get(i));
        }
        collection.mergeCollections("source", "dest");
        int total = collection.size("dest");
        List<Location> all = collection.getTopK("dest", total);
        for (int i = 0; i < all.size() - 1; i++) {
            assertTrue(all.get(i).ranking >= all.get(i + 1).ranking);
        }
    }

    // --- exception tests ---

    @Example
    void addDuplicateThrows() {
        collection = createCollection();
        collection.addLocation("coffee", "Starbucks", 8.0);
        assertThrows(IllegalArgumentException.class, () ->
                collection.addLocation("coffee", "Starbucks", 5.0));
    }

    @Example
    void addRankingTooHighThrows() {
        collection = createCollection();
        assertThrows(IllegalArgumentException.class, () ->
                collection.addLocation("coffee", "Starbucks", 10.1));
    }

    @Example
    void addLocationRankingNegativeThrows() {
        collection = createCollection();
        assertThrows(IllegalArgumentException.class, () ->
                collection.addLocation("coffee", "Starbucks", -0.1));
    }

    @Example
    void removeNonExistentCollectionThrows() {
        collection = createCollection();
        assertThrows(IllegalArgumentException.class, () ->
                collection.removeLocation("coffee", "Starbucks"));
    }

    @Example
    void removeLocationNonExistentLocationThrows() {
        collection = createCollection();
        collection.addLocation("coffee", "Starbucks", 8.0);
        assertThrows(IllegalArgumentException.class, () ->
                collection.removeLocation("coffee", "Ghost Cafe"));
    }

    @Example
    void containsNonExistentCollectionThrows() {
        collection = createCollection();
        assertThrows(IllegalArgumentException.class, () ->
                collection.contains("ghost", "Starbucks"));
    }

    @Example
    void getTopKNonExistentCollectionThrows() {
        collection = createCollection();
        assertThrows(IllegalArgumentException.class, () ->
                collection.getTopK("ghost", 3));
    }

    @Example
    void updateRankingNonExistentCollectionThrows() {
        collection = createCollection();
        assertThrows(IllegalArgumentException.class, () ->
                collection.updateRanking("ghost", "Starbucks", 5.0));
    }

    @Example
    void updateRankingNonExistentLocationThrows() {
        collection = createCollection();
        collection.addLocation("coffee", "Starbucks", 8.0);
        assertThrows(IllegalArgumentException.class, () ->
                collection.updateRanking("coffee", "Ghost Cafe", 5.0));
    }

    @Example
    void updateRankingTooHighThrows() {
        collection = createCollection();
        collection.addLocation("coffee", "Starbucks", 8.0);
        assertThrows(IllegalArgumentException.class, () ->
                collection.updateRanking("coffee", "Starbucks", 10.1));
    }

    @Example
    void updateRankingNegativeThrows() {
        collection = createCollection();
        collection.addLocation("coffee", "Starbucks", 8.0);
        assertThrows(IllegalArgumentException.class, () ->
                collection.updateRanking("coffee", "Starbucks", -0.1));
    }

    @Example
    void mergeNonExistentSourceThrows() {
        collection = createCollection();
        collection.addLocation("coffee", "Starbucks", 8.0);
        assertThrows(IllegalArgumentException.class, () ->
                collection.mergeCollections("ghost", "coffee"));
    }

    @Example
    void mergeNonExistentDestinationThrows() {
        collection = createCollection();
        collection.addLocation("coffee", "Starbucks", 8.0);
        assertThrows(IllegalArgumentException.class, () ->
                collection.mergeCollections("coffee", "ghost"));
    }

    @Example
    void mergeBothNonExistentThrows() {
        collection = createCollection();
        assertThrows(IllegalArgumentException.class, () ->
                collection.mergeCollections("ghost1", "ghost2"));
    }

    @Example
    void mergeRemovesSourceCollection() {
        collection = createCollection();
        collection.addLocation("coffee", "A", 8.0);
        collection.addLocation("tea", "B", 9.0);
        collection.mergeCollections("tea", "coffee");
        assertThrows(IllegalArgumentException.class, () ->
                collection.contains("tea", "B"));
    }
}
