package collections;

import java.util.List;


/**
 * Represents a manager for named collections of named locations.
 * Each collection is named and contains many locations, all with their
 * own names and rankings between 0 and 10. Supports insertion, deletion,
 * retrival of top ranked locations, contains, size checking, ranking modification, and collection
 * merging.
 */
public interface Collection {

    /**
     * Adds a location to a specific collection with a 0-10 ranking
     *
     * @throws IllegalArgumentException if location already exists in collection,
     *  or if ranking is not between 0 and 10 inclusive
     * @param collection
     * @param location
     * @param ranking
     */
    void addLocation(String collection, String location, double ranking);

    /**
     * Removes a location with given name from a specific collection
     *
     * @throws IllegalArgumentException if collection does not exist,
     *  or location does not exist within the collection
     * @param collection
     * @param location
     */
    void removeLocation(String collection, String location);

    /**
     * Checks whether a location exists within a given collection
     *
     * @throws IllegalArgumentException if collection does not exist
     * @param collection
     * @param location
     * @return true if the location exists in the collection
     */
    boolean contains(String collection, String location);

    /**
     * Returns the number of locations within a collection
     *
     * @param collection
     * @return the number of locations
     */
    int size(String collection);

    /**
     * Gets the top k ranked locations from the given collection, if k exceeds
     * size, gets all locations
     *
     * @throws IllegalArgumentException if collection does not exist
     * @param collection
     * @param k
     * @return List<Location>
     */
    List<Location> getTopK(String collection, int k);

    /**
     * Updates ranking of specific location in the given collection
     *
     * @throws IllegalArgumentException if collection does not exist,
     *  or if location does not exist in the collection,
     *  or if ranking is not between 0 and 10 inclusive
     * @param collection
     * @param location
     * @param newRanking
     */
    void updateRanking(String collection, String location, double newRanking);

    /**
     * Merges source into destination, preserving all locations and rankings, and deleting
     * source collection. If both contain same location, keeps destination version.
     *
     * @throws IllegalArgumentException if either collection does not exist
     * @param sourceCollection
     * @param destinationCollection
     */
    void mergeCollections(String sourceCollection, String destinationCollection);

    /**
     * Location defines a location with a name and a rating
     */
    static public class Location implements Comparable<Location> {
        public final String name;

        // ranking from 0 to 10 -- can be changed
        public double ranking;

        public Location(String name, double ranking) {
            this.name = name;
            this.ranking = ranking;
        }

        /**
         * comparison using ranking.
         * @return int: comparison value
         */
        @Override
        public int compareTo(Location other) {
            return Double.compare(this.ranking, other.ranking);
        }
    }

}
