package collections;

public class ResortedArrayCollectionTests extends CollectionTests {
    @Override
    public Collection createCollection() {
        return new ResortedArrayCollection();
    }
}
