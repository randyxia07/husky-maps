package collections;

public class HashMapCollectionTests extends CollectionTests {
    @Override
    public Collection createCollection() {
        return new HashMapCollection();
    }
}
