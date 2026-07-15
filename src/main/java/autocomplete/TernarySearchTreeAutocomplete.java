package autocomplete;
 
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
 
/**
 * Ternary search tree (TST) implementation of the {@link Autocomplete} interface.
 *
 * @see Autocomplete
 */
public class TernarySearchTreeAutocomplete implements Autocomplete {
    /**
     * The overall root of the tree: the first character of the first autocompletion term added to this tree.
     */
    private Node overallRoot;
 
    /**
     * Constructs an empty instance.
     */
    public TernarySearchTreeAutocomplete() {
        overallRoot = null;
    }
 
    /**
     * Constructs an instance containing the given terms.
     */
    public TernarySearchTreeAutocomplete(Collection<? extends CharSequence> terms) {
        this();
        addAll(terms);
    }
 
    @Override
    public void addAll(Collection<? extends CharSequence> terms) {
        for (CharSequence term : terms) {
            if (term != null && term.length() > 0) {
                overallRoot = put(overallRoot, term, 0);
            }
        }
    }
 
    /**
     * Recursively inserts the given term into the TST rooted at the given node,
     * starting at character index {@code index} of the term.
     *
     * @param node  current TST node (may be null)
     * @param term  the term being inserted
     * @param index the current character position in the term
     * @return the (possibly new) root of this subtree
     */
    private Node put(Node node, CharSequence term, int index) {
        char currentChar = term.charAt(index);
 
        if (node == null) {
            node = new Node(currentChar);
        }
 
        if (currentChar < node.data) {
            node.left = put(node.left, term, index);
        } else if (currentChar > node.data) {
            node.right = put(node.right, term, index);
        } else {
            // Characters match: advance to the next character if one exists
            if (index + 1 < term.length()) {
                node.mid = put(node.mid, term, index + 1);
            } else {
                // We've placed the last character of the term
                node.isTerm = true;
            }
        }
        return node;
    }
 
    @Override
    public List<CharSequence> allMatches(CharSequence prefix) {
        List<CharSequence> result = new ArrayList<>();
        if (prefix == null || prefix.length() == 0) {
            return result;
        }
 
        // Navigate to the node corresponding to the last character of the prefix.
        Node prefixEndNode = get(overallRoot, prefix, 0);
        if (prefixEndNode == null) {
            return result;
        }
 
        // If the prefix itself is a complete term, add it. 
        if (prefixEndNode.isTerm) {
            result.add(prefix.toString());
        }
 
        // Collect all terms in the subtree hanging off prefixEndNode.mid,
        // which represent all completions of the prefix.
        collect(prefixEndNode.mid, new StringBuilder(prefix), result);
        return result;
    }
 
    /**
     * Returns the node in the TST corresponding to the last character of the given prefix,
     * or null if the prefix is not present in the TST.
     *
     * @param node   current TST node
     * @param prefix the prefix to search for
     * @param index  current character index in the prefix
     * @return node matching the last character of the prefix, or null
     */
    private Node get(Node node, CharSequence prefix, int index) {
        if (node == null) {
            return null;
        }
        char currentChar = prefix.charAt(index);
 
        if (currentChar < node.data) {
            return get(node.left, prefix, index);
        } else if (currentChar > node.data) {
            return get(node.right, prefix, index);
        } else {
            // Characters match
            if (index + 1 == prefix.length()) {
                // Reached the last character of the prefix
                return node;
            }
            return get(node.mid, prefix, index + 1);
        }
    }
 
    /**
     * Collects all complete terms in the subtree rooted at {@code node},
     * appending them to {@code result}. The {@code prefix} StringBuilder
     * tracks the characters accumulated so far on the path from the prefix
     * end node down into this subtree.
     *
     * @param node   current TST node
     * @param prefix characters accumulated so far (including the original query prefix)
     * @param result list to add complete terms to
     */
    private void collect(Node node, StringBuilder prefix, List<CharSequence> result) {
        if (node == null) {
            return;
        }
 
        // Explore the left subtree (same depth / character position)
        collect(node.left, prefix, result);
 
        // Visit the current node's character
        prefix.append(node.data);
        if (node.isTerm) {
            result.add(prefix.toString());
        }
        // Explore completions via the middle child (next character position)
        collect(node.mid, prefix, result);
        // Backtrack
        prefix.deleteCharAt(prefix.length() - 1);
 
        // Explore the right subtree (same depth / character position)
        collect(node.right, prefix, result);
    }
 
    /**
     * A search tree node representing a single character in an autocompletion term.
     */
    private static class Node {
        private final char data;
        private boolean isTerm;
        private Node left;
        private Node mid;
        private Node right;
 
        public Node(char data) {
            this.data = data;
            this.isTerm = false;
            this.left = null;
            this.mid = null;
            this.right = null;
        }
    }
}