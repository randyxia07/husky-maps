package collections;

import net.jqwik.api.Example;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests specifically for {@link HashMapCollection}.
 * Also compares against {@link ResortedArrayCollection} to verify
 * both implementations produce identical results.
 */
public class HashMapCollectionTests {

    private Collection improved;
    private Collection simple;

    @BeforeEach
    void setUp() {
        improved = new HashMapCollection();
        simple = new ResortedArrayCollection();
    }

    // --- addLocation ---

    @Test
    void addLocationToNewCollection() {
        improved.addLocation("Seattle", "Space Needle", 8.5);
        assertEquals(1, improved.getTopK("Seattle", 10).size());
        assertEquals("Space Needle", improved.getTopK("Seattle", 1).get(0).name);
        assertEquals(8.5, improved.getTopK("Seattle", 1).get(0).ranking);
    }

    @Test
    void addMultipleLocations() {
        improved.addLocation("Seattle", "Space Needle", 8.5);
        improved.addLocation("Seattle", "Pike Place", 9.0);
        improved.addLocation("Seattle", "Kerry Park", 7.5);
        assertEquals(3, improved.getTopK("Seattle", 10).size());
    }

    @Test
    void addLocationToMultipleCollections() {
        improved.addLocation("Seattle", "Space Needle", 8.5);
        improved.addLocation("Portland", "Powell's Books", 9.2);
        assertEquals(1, improved.getTopK("Seattle", 10).size());
        assertEquals(1, improved.getTopK("Portland", 10).size());
    }

    @Test
    void addDuplicateLocationThrows() {
        improved.addLocation("Seattle", "Space Needle", 8.5);
        assertThrows(IllegalArgumentException.class,
                () -> improved.addLocation("Seattle", "Space Needle", 7.0));
    }

    @Test
    void addLocationPreservesExistingCollection() {
        improved.addLocation("Seattle", "Space Needle", 8.5);
        improved.addLocation("Seattle", "Pike Place", 9.0);
        // Adding a third location should not wipe the first two
        improved.addLocation("Seattle", "Kerry Park", 7.5);
        assertEquals(3, improved.getTopK("Seattle", 10).size());
    }

    @Test
    void addLocationAtBoundaryRankings() {
        improved.addLocation("Seattle", "Low", 0.0);
        improved.addLocation("Seattle", "High", 10.0);
        assertEquals("High", improved.getTopK("Seattle", 1).get(0).name);
        assertEquals(0.0, improved.getTopK("Seattle", 2).get(1).ranking);
    }

    // --- removeLocation ---

    @Test
    void removeLocationDecreasesSize() {
        improved.addLocation("Seattle", "Space Needle", 8.5);
        improved.addLocation("Seattle", "Pike Place", 9.0);
        improved.removeLocation("Seattle", "Space Needle");
        assertEquals(1, improved.getTopK("Seattle", 10).size());
        assertEquals("Pike Place", improved.getTopK("Seattle", 1).get(0).name);
    }

    @Test
    void removeOnlyLocation() {
        improved.addLocation("Seattle", "Space Needle", 8.5);
        improved.removeLocation("Seattle", "Space Needle");
        assertEquals(0, improved.getTopK("Seattle", 10).size());
    }

    @Test
    void removeNonExistentLocationDoesNotThrow() {
        improved.addLocation("Seattle", "Space Needle", 8.5);
        assertDoesNotThrow(() -> improved.removeLocation("Seattle", "Nonexistent"));
        // Original location should still be there
        assertEquals(1, improved.getTopK("Seattle", 10).size());
    }

    @Test
    void removeFromNonExistentCollectionThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> improved.removeLocation("Nonexistent", "Space Needle"));
    }

    @Test
    void canAddLocationAfterRemoval() {
        improved.addLocation("Seattle", "Space Needle", 8.5);
        improved.removeLocation("Seattle", "Space Needle");
        assertDoesNotThrow(() -> improved.addLocation("Seattle", "Space Needle", 7.0));
        assertEquals(1, improved.getTopK("Seattle", 10).size());
    }

    // --- getTopK ---

    @Test
    void getTopKReturnsCorrectOrder() {
        improved.addLocation("Seattle", "Kerry Park", 7.5);
        improved.addLocation("Seattle", "Pike Place", 9.0);
        improved.addLocation("Seattle", "Space Needle", 8.5);
        improved.addLocation("Seattle", "Gas Works", 6.0);

        List<Collection.Location> top3 = improved.getTopK("Seattle", 3);
        assertEquals(3, top3.size());
        assertEquals("Pike Place", top3.get(0).name);
        assertEquals("Space Needle", top3.get(1).name);
        assertEquals("Kerry Park", top3.get(2).name);
    }

    @Test
    void getTopKExceedingSizeReturnsAll() {
        improved.addLocation("Seattle", "Space Needle", 8.5);
        improved.addLocation("Seattle", "Pike Place", 9.0);
        assertEquals(2, improved.getTopK("Seattle", 100).size());
    }

    @Test
    void getTopKOnEmptyCollectionReturnsEmpty() {
        improved.addLocation("Seattle", "Space Needle", 8.5);
        improved.removeLocation("Seattle", "Space Needle");
        assertEquals(0, improved.getTopK("Seattle", 5).size());
    }

    @Test
    void getTopKNonExistentCollectionThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> improved.getTopK("Nonexistent", 5));
    }

    @Test
    void getTopKReturnsCorrectRankings() {
        improved.addLocation("Seattle", "Space Needle", 8.5);
        improved.addLocation("Seattle", "Pike Place", 9.0);
        List<Collection.Location> top = improved.getTopK("Seattle", 2);
        assertEquals(9.0, top.get(0).ranking);
        assertEquals(8.5, top.get(1).ranking);
    }

    // --- updateRanking ---

    @Test
    void updateRankingChangesOrder() {
        improved.addLocation("Seattle", "Space Needle", 8.5);
        improved.addLocation("Seattle", "Pike Place", 9.0);
        improved.updateRanking("Seattle", "Space Needle", 9.5);
        assertEquals("Space Needle", improved.getTopK("Seattle", 1).get(0).name);
    }

    @Test
    void updateRankingReflectsNewValue() {
        improved.addLocation("Seattle", "Space Needle", 8.5);
        improved.updateRanking("Seattle", "Space Needle", 6.0);
        assertEquals(6.0, improved.getTopK("Seattle", 1).get(0).ranking);
    }

    @Test
    void updateRankingNonExistentCollectionThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> improved.updateRanking("Nonexistent", "Space Needle", 5.0));
    }

    @Test
    void updateRankingNonExistentLocationDoesNotThrow() {
        improved.addLocation("Seattle", "Space Needle", 8.5);
        assertDoesNotThrow(() -> improved.updateRanking("Seattle", "Nonexistent", 5.0));
        // Existing location should be unchanged
        assertEquals(8.5, improved.getTopK("Seattle", 1).get(0).ranking);
    }

    // --- mergeCollections ---

    @Test
    void mergeCollectionsCombinesLocations() {
        improved.addLocation("Seattle", "Space Needle", 8.5);
        improved.addLocation("Portland", "Powell's Books", 9.2);
        improved.mergeCollections("Portland", "Seattle");
        assertEquals(2, improved.getTopK("Seattle", 10).size());
    }

    @Test
    void mergeCollectionsDeletesSource() {
        improved.addLocation("Seattle", "Space Needle", 8.5);
        improved.addLocation("Portland", "Powell's Books", 9.2);
        improved.mergeCollections("Portland", "Seattle");
        assertThrows(IllegalArgumentException.class,
                () -> improved.getTopK("Portland", 1));
    }

    @Test
    void mergeCollectionsKeepsDestinationOnConflict() {
        improved.addLocation("Seattle", "Space Needle", 8.5);
        improved.addLocation("Portland", "Space Needle", 5.0);
        improved.mergeCollections("Portland", "Seattle");
        // Destination ranking (8.5) should be preserved
        assertEquals(8.5, improved.getTopK("Seattle", 1).get(0).ranking);
        assertEquals(1, improved.getTopK("Seattle", 10).size());
    }

    @Test
    void mergeCollectionsNonExistentSourceThrows() {
        improved.addLocation("Seattle", "Space Needle", 8.5);
        assertThrows(IllegalArgumentException.class,
                () -> improved.mergeCollections("Nonexistent", "Seattle"));
    }

    @Test
    void mergeCollectionsNonExistentDestinationThrows() {
        improved.addLocation("Seattle", "Space Needle", 8.5);
        assertThrows(IllegalArgumentException.class,
                () -> improved.mergeCollections("Seattle", "Nonexistent"));
    }

    // --- Agrees with simple implementation ---

    @Test
    void agreesWithSimpleOnTopKAfterAdds() {
        simple.addLocation("Seattle", "Kerry Park", 7.5);
        simple.addLocation("Seattle", "Pike Place", 9.0);
        simple.addLocation("Seattle", "Space Needle", 8.5);
        improved.addLocation("Seattle", "Kerry Park", 7.5);
        improved.addLocation("Seattle", "Pike Place", 9.0);
        improved.addLocation("Seattle", "Space Needle", 8.5);

        List<Collection.Location> simpleResult = simple.getTopK("Seattle", 3);
        List<Collection.Location> improvedResult = improved.getTopK("Seattle", 3);

        assertEquals(simpleResult.size(), improvedResult.size());
        for (int i = 0; i < simpleResult.size(); i++) {
            assertEquals(simpleResult.get(i).name, improvedResult.get(i).name);
            assertEquals(simpleResult.get(i).ranking, improvedResult.get(i).ranking);
        }
    }

    @Test
    void agreesWithSimpleAfterUpdate() {
        simple.addLocation("Seattle", "Space Needle", 8.5);
        simple.addLocation("Seattle", "Pike Place", 9.0);
        simple.updateRanking("Seattle", "Space Needle", 9.8);
        improved.addLocation("Seattle", "Space Needle", 8.5);
        improved.addLocation("Seattle", "Pike Place", 9.0);
        improved.updateRanking("Seattle", "Space Needle", 9.8);

        List<Collection.Location> simpleResult = simple.getTopK("Seattle", 2);
        List<Collection.Location> improvedResult = improved.getTopK("Seattle", 2);

        assertEquals(simpleResult.get(0).name, improvedResult.get(0).name);
        assertEquals(simpleResult.get(1).name, improvedResult.get(1).name);
    }

    @Test
    void agreesWithSimpleAfterMerge() {
        simple.addLocation("Seattle", "Space Needle", 8.5);
        simple.addLocation("Portland", "Powell's Books", 9.2);
        simple.addLocation("Portland", "Space Needle", 5.0);
        simple.mergeCollections("Portland", "Seattle");
        improved.addLocation("Seattle", "Space Needle", 8.5);
        improved.addLocation("Portland", "Powell's Books", 9.2);
        improved.addLocation("Portland", "Space Needle", 5.0);
        improved.mergeCollections("Portland", "Seattle");

        List<Collection.Location> simpleResult = simple.getTopK("Seattle", 10);
        List<Collection.Location> improvedResult = improved.getTopK("Seattle", 10);

        assertEquals(simpleResult.size(), improvedResult.size());
        for (int i = 0; i < simpleResult.size(); i++) {
            assertEquals(simpleResult.get(i).name, improvedResult.get(i).name);
        }
    }

    // --- Full workflow ---

    @Example
    void fullWorkflow() {
        improved = new HashMapCollection();
        improved.addLocation("Seattle", "Space Needle", 8.5);
        improved.addLocation("Seattle", "Pike Place", 9.0);
        improved.addLocation("Seattle", "Kerry Park", 7.5);
        improved.addLocation("Portland", "Powell's Books", 9.2);
        improved.addLocation("Portland", "Multnomah Falls", 8.8);

        // Top 2 from Seattle
        List<Collection.Location> top2 = improved.getTopK("Seattle", 2);
        assertEquals(2, top2.size());
        assertEquals("Pike Place", top2.get(0).name);
        assertEquals("Space Needle", top2.get(1).name);

        // Update a ranking and verify new order
        improved.updateRanking("Seattle", "Kerry Park", 9.9);
        assertEquals("Kerry Park", improved.getTopK("Seattle", 1).get(0).name);

        // Remove a location
        improved.removeLocation("Seattle", "Pike Place");
        assertEquals(2, improved.getTopK("Seattle", 10).size());

        // Merge Portland into Seattle
        improved.mergeCollections("Portland", "Seattle");
        assertEquals(4, improved.getTopK("Seattle", 10).size());
        assertThrows(IllegalArgumentException.class,
                () -> improved.getTopK("Portland", 1));
    }
}
