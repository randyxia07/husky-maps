package collections;

import net.jqwik.api.Example;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Shared tests for {@link ResortedArrayCollection} and {@link HashMapCollection}.
 * Both implementations must produce identical results for all operations.
 */
public class CollectionTests {

    private Collection simple;
    private Collection improved;

    @BeforeEach
    void setUp() {
        simple = new ResortedArrayCollection();
        improved = new HashMapCollection();
    }

    // --- addLocation ---

    @Test
    void addLocationCreatesCollectionImplicitly() {
        simple.addLocation("Seattle", "Space Needle", 8.5);
        improved.addLocation("Seattle", "Space Needle", 8.5);

        assertEquals(1, simple.getTopK("Seattle", 10).size());
        assertEquals(1, improved.getTopK("Seattle", 10).size());
    }

    @Test
    void addMultipleLocationsToSameCollection() {
        simple.addLocation("Seattle", "Space Needle", 8.5);
        simple.addLocation("Seattle", "Pike Place", 9.0);
        improved.addLocation("Seattle", "Space Needle", 8.5);
        improved.addLocation("Seattle", "Pike Place", 9.0);

        assertEquals(2, simple.getTopK("Seattle", 10).size());
        assertEquals(2, improved.getTopK("Seattle", 10).size());
    }

    @Test
    void addLocationToMultipleCollections() {
        simple.addLocation("Seattle", "Space Needle", 8.5);
        simple.addLocation("Portland", "Powell's Books", 9.2);
        improved.addLocation("Seattle", "Space Needle", 8.5);
        improved.addLocation("Portland", "Powell's Books", 9.2);

        assertEquals(1, simple.getTopK("Seattle", 10).size());
        assertEquals(1, simple.getTopK("Portland", 10).size());
        assertEquals(1, improved.getTopK("Seattle", 10).size());
        assertEquals(1, improved.getTopK("Portland", 10).size());
    }

    @Test
    void addDuplicateLocationThrows() {
        simple.addLocation("Seattle", "Space Needle", 8.5);
        improved.addLocation("Seattle", "Space Needle", 8.5);

        assertThrows(IllegalArgumentException.class,
                () -> simple.addLocation("Seattle", "Space Needle", 7.0));
        assertThrows(IllegalArgumentException.class,
                () -> improved.addLocation("Seattle", "Space Needle", 7.0));
    }

    @Test
    void addLocationAtBoundaryRankings() {
        simple.addLocation("Seattle", "Low", 0.0);
        simple.addLocation("Seattle", "High", 10.0);
        improved.addLocation("Seattle", "Low", 0.0);
        improved.addLocation("Seattle", "High", 10.0);

        assertEquals("High", simple.getTopK("Seattle", 1).get(0).name);
        assertEquals("High", improved.getTopK("Seattle", 1).get(0).name);
    }

    // --- removeLocation ---

    @Test
    void removeLocationDecreasesSize() {
        simple.addLocation("Seattle", "Space Needle", 8.5);
        simple.addLocation("Seattle", "Pike Place", 9.0);
        simple.removeLocation("Seattle", "Space Needle");
        improved.addLocation("Seattle", "Space Needle", 8.5);
        improved.addLocation("Seattle", "Pike Place", 9.0);
        improved.removeLocation("Seattle", "Space Needle");

        assertEquals(1, simple.getTopK("Seattle", 10).size());
        assertEquals(1, improved.getTopK("Seattle", 10).size());
        assertEquals("Pike Place", simple.getTopK("Seattle", 1).get(0).name);
        assertEquals("Pike Place", improved.getTopK("Seattle", 1).get(0).name);
    }

    @Test
    void removeNonExistentLocationDoesNotThrow() {
        simple.addLocation("Seattle", "Space Needle", 8.5);
        improved.addLocation("Seattle", "Space Needle", 8.5);

        assertDoesNotThrow(() -> simple.removeLocation("Seattle", "Nonexistent"));
        assertDoesNotThrow(() -> improved.removeLocation("Seattle", "Nonexistent"));
    }

    @Test
    void removeLocationFromNonExistentCollectionThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> simple.removeLocation("Nonexistent", "Space Needle"));
        assertThrows(IllegalArgumentException.class,
                () -> improved.removeLocation("Nonexistent", "Space Needle"));
    }

    // --- getTopK ---

    @Test
    void getTopKReturnsHighestRankedFirst() {
        simple.addLocation("Seattle", "Kerry Park", 7.5);
        simple.addLocation("Seattle", "Pike Place", 9.0);
        simple.addLocation("Seattle", "Space Needle", 8.5);
        improved.addLocation("Seattle", "Kerry Park", 7.5);
        improved.addLocation("Seattle", "Pike Place", 9.0);
        improved.addLocation("Seattle", "Space Needle", 8.5);

        List<Collection.Location> simpleTop = simple.getTopK("Seattle", 2);
        List<Collection.Location> improvedTop = improved.getTopK("Seattle", 2);

        assertEquals(2, simpleTop.size());
        assertEquals(2, improvedTop.size());
        assertEquals("Pike Place", simpleTop.get(0).name);
        assertEquals("Pike Place", improvedTop.get(0).name);
        assertEquals("Space Needle", simpleTop.get(1).name);
        assertEquals("Space Needle", improvedTop.get(1).name);
    }

    @Test
    void getTopKExceedingSizeReturnsAll() {
        simple.addLocation("Seattle", "Space Needle", 8.5);
        simple.addLocation("Seattle", "Pike Place", 9.0);
        improved.addLocation("Seattle", "Space Needle", 8.5);
        improved.addLocation("Seattle", "Pike Place", 9.0);

        assertEquals(2, simple.getTopK("Seattle", 100).size());
        assertEquals(2, improved.getTopK("Seattle", 100).size());
    }

    @Test
    void getTopKOnEmptyCollectionReturnsEmpty() {
        simple.addLocation("Seattle", "Space Needle", 8.5);
        simple.removeLocation("Seattle", "Space Needle");
        improved.addLocation("Seattle", "Space Needle", 8.5);
        improved.removeLocation("Seattle", "Space Needle");

        assertEquals(0, simple.getTopK("Seattle", 5).size());
        assertEquals(0, improved.getTopK("Seattle", 5).size());
    }

    @Test
    void getTopKNonExistentCollectionThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> simple.getTopK("Nonexistent", 5));
        assertThrows(IllegalArgumentException.class,
                () -> improved.getTopK("Nonexistent", 5));
    }

    @Test
    void getTopKReturnsCorrectRankings() {
        simple.addLocation("Seattle", "Space Needle", 8.5);
        improved.addLocation("Seattle", "Space Needle", 8.5);

        assertEquals(8.5, simple.getTopK("Seattle", 1).get(0).ranking);
        assertEquals(8.5, improved.getTopK("Seattle", 1).get(0).ranking);
    }

    // --- updateRanking ---

    @Test
    void updateRankingChangesOrder() {
        simple.addLocation("Seattle", "Space Needle", 8.5);
        simple.addLocation("Seattle", "Pike Place", 9.0);
        simple.updateRanking("Seattle", "Space Needle", 9.5);
        improved.addLocation("Seattle", "Space Needle", 8.5);
        improved.addLocation("Seattle", "Pike Place", 9.0);
        improved.updateRanking("Seattle", "Space Needle", 9.5);

        assertEquals("Space Needle", simple.getTopK("Seattle", 1).get(0).name);
        assertEquals("Space Needle", improved.getTopK("Seattle", 1).get(0).name);
    }

    @Test
    void updateRankingNonExistentCollectionThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> simple.updateRanking("Nonexistent", "Space Needle", 5.0));
        assertThrows(IllegalArgumentException.class,
                () -> improved.updateRanking("Nonexistent", "Space Needle", 5.0));
    }

    @Test
    void updateRankingNonExistentLocationDoesNotThrow() {
        simple.addLocation("Seattle", "Space Needle", 8.5);
        improved.addLocation("Seattle", "Space Needle", 8.5);

        assertDoesNotThrow(() -> simple.updateRanking("Seattle", "Nonexistent", 5.0));
        assertDoesNotThrow(() -> improved.updateRanking("Seattle", "Nonexistent", 5.0));
    }

    // --- mergeCollections ---

    @Test
    void mergeCollectionsMovesLocations() {
        simple.addLocation("Seattle", "Space Needle", 8.5);
        simple.addLocation("Portland", "Powell's Books", 9.2);
        simple.mergeCollections("Portland", "Seattle");
        improved.addLocation("Seattle", "Space Needle", 8.5);
        improved.addLocation("Portland", "Powell's Books", 9.2);
        improved.mergeCollections("Portland", "Seattle");

        assertEquals(2, simple.getTopK("Seattle", 10).size());
        assertEquals(2, improved.getTopK("Seattle", 10).size());
    }

    @Test
    void mergeCollectionsDeletesSource() {
        simple.addLocation("Seattle", "Space Needle", 8.5);
        simple.addLocation("Portland", "Powell's Books", 9.2);
        simple.mergeCollections("Portland", "Seattle");
        improved.addLocation("Seattle", "Space Needle", 8.5);
        improved.addLocation("Portland", "Powell's Books", 9.2);
        improved.mergeCollections("Portland", "Seattle");

        assertThrows(IllegalArgumentException.class,
                () -> simple.getTopK("Portland", 1));
        assertThrows(IllegalArgumentException.class,
                () -> improved.getTopK("Portland", 1));
    }

    @Test
    void mergeCollectionsKeepsDestinationOnConflict() {
        simple.addLocation("Seattle", "Space Needle", 8.5);
        simple.addLocation("Portland", "Space Needle", 5.0);
        simple.mergeCollections("Portland", "Seattle");
        improved.addLocation("Seattle", "Space Needle", 8.5);
        improved.addLocation("Portland", "Space Needle", 5.0);
        improved.mergeCollections("Portland", "Seattle");

        // Destination ranking (8.5) should be preserved, not source (5.0)
        assertEquals(8.5, simple.getTopK("Seattle", 1).get(0).ranking);
        assertEquals(8.5, improved.getTopK("Seattle", 1).get(0).ranking);
        assertEquals(1, simple.getTopK("Seattle", 10).size());
        assertEquals(1, improved.getTopK("Seattle", 10).size());
    }

    @Test
    void mergeCollectionsNonExistentCollectionThrows() {
        simple.addLocation("Seattle", "Space Needle", 8.5);
        improved.addLocation("Seattle", "Space Needle", 8.5);

        assertThrows(IllegalArgumentException.class,
                () -> simple.mergeCollections("Nonexistent", "Seattle"));
        assertThrows(IllegalArgumentException.class,
                () -> improved.mergeCollections("Nonexistent", "Seattle"));
        assertThrows(IllegalArgumentException.class,
                () -> simple.mergeCollections("Seattle", "Nonexistent"));
        assertThrows(IllegalArgumentException.class,
                () -> improved.mergeCollections("Seattle", "Nonexistent"));
    }

    // --- Both implementations agree ---

    @Test
    void bothImplementationsAgreeOnTopK() {
        simple.addLocation("Seattle", "Kerry Park", 7.5);
        simple.addLocation("Seattle", "Pike Place", 9.0);
        simple.addLocation("Seattle", "Space Needle", 8.5);
        simple.addLocation("Seattle", "Gas Works", 6.0);
        improved.addLocation("Seattle", "Kerry Park", 7.5);
        improved.addLocation("Seattle", "Pike Place", 9.0);
        improved.addLocation("Seattle", "Space Needle", 8.5);
        improved.addLocation("Seattle", "Gas Works", 6.0);

        List<Collection.Location> simpleResult = simple.getTopK("Seattle", 3);
        List<Collection.Location> improvedResult = improved.getTopK("Seattle", 3);

        assertEquals(simpleResult.size(), improvedResult.size());
        for (int i = 0; i < simpleResult.size(); i++) {
            assertEquals(simpleResult.get(i).name, improvedResult.get(i).name);
            assertEquals(simpleResult.get(i).ranking, improvedResult.get(i).ranking);
        }
    }

    @Test
    void bothImplementationsAgreeAfterUpdate() {
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

    // --- Full workflow ---

    @Example
    void fullWorkflow() {
        Collection c = new HashMapCollection();

        c.addLocation("Seattle", "Space Needle", 8.5);
        c.addLocation("Seattle", "Pike Place", 9.0);
        c.addLocation("Seattle", "Kerry Park", 7.5);
        c.addLocation("Portland", "Powell's Books", 9.2);
        c.addLocation("Portland", "Multnomah Falls", 8.8);

        // Top 2 from Seattle
        List<Collection.Location> top2 = c.getTopK("Seattle", 2);
        assertEquals(2, top2.size());
        assertEquals("Pike Place", top2.get(0).name);
        assertEquals("Space Needle", top2.get(1).name);

        // Update a ranking and verify new order
        c.updateRanking("Seattle", "Kerry Park", 9.9);
        assertEquals("Kerry Park", c.getTopK("Seattle", 1).get(0).name);

        // Remove a location
        c.removeLocation("Seattle", "Pike Place");
        assertEquals(2, c.getTopK("Seattle", 10).size());

        // Merge Portland into Seattle
        c.mergeCollections("Portland", "Seattle");
        assertEquals(4, c.getTopK("Seattle", 10).size());
        assertThrows(IllegalArgumentException.class, () -> c.getTopK("Portland", 1));
    }
}
