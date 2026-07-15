package autocomplete;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Binary search implementation of the {@link Autocomplete} interface.
 *
 * @see Autocomplete
 */
public class BinarySearchAutocomplete implements Autocomplete {
    /**
     * {@link List} of added autocompletion terms.
     */
    private final List<CharSequence> elements;

    /**
     * Constructs an empty instance.
     */
    public BinarySearchAutocomplete() {
        elements = new ArrayList<>();
    }

    /**
     * Constructs an instance containing the given terms.
     */
    public BinarySearchAutocomplete(Collection<? extends CharSequence> terms) {
        this();
        addAll(terms);
    }

    @Override
    public void addAll(Collection<? extends CharSequence> terms) {
        // TODO: Replace with your code
        elements.addAll(terms);
        Collections.sort(elements, CharSequence::compare);
    }

    @Override
    public List<CharSequence> allMatches(CharSequence prefix) {
        // TODO: Replace with your code
        List<CharSequence> result = new ArrayList<>();
        if (prefix == null || prefix.length() == 0) {
            return result;
        }
 
        // Binary search for the insertion point of the prefix.
        int i = Collections.binarySearch(elements, prefix, CharSequence::compare);
 
        // If not found exactly, binarySearch returns -(insertion point) - 1.
        // Convert to the insertion point index.
        if (i < 0) {
            i = -(i + 1);
        }
 
        // Iterate forward from that index, collecting all prefix matches.
        while (i < elements.size() && Autocomplete.isPrefixOf(prefix, elements.get(i))) {
            result.add(elements.get(i));
            i++;
        }
        return result;
    }
}
