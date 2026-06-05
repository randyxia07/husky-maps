package minpq;

import java.util.*;

/**
 * Optimized binary heap implementation of the {@link MinPQ} interface.
 *
 * @param <E> the type of elements in this priority queue.
 * @see MinPQ
 */
public class OptimizedHeapMinPQ<E> implements MinPQ<E> {
    /**
     * {@link List} of {@link PriorityNode} objects representing the heap of element-priority pairs.
     */
    private final List<PriorityNode<E>> elements;
    /**
     * {@link Map} of each element to its associated index in the {@code elements} heap.
     */
    private final Map<E, Integer> elementsToIndex;

    /**
     * Constructs an empty instance.
     */
    public OptimizedHeapMinPQ() {
        elements = new ArrayList<>();
        elementsToIndex = new HashMap<>();
        elements.add(null);
    }

    /**
     * Constructs an instance containing all the given elements and their priority values.
     *
     * @param elementsAndPriorities each element and its corresponding priority.
     */
    public OptimizedHeapMinPQ(Map<E, Double> elementsAndPriorities) {
        // TODO: Replace with your code
        elements = new ArrayList<>(elementsAndPriorities.size() + 1);
        elementsToIndex = new HashMap<>(elementsAndPriorities.size());
        elements.add(null); // 1-indexed placeholder
 
        // Add all elements into the array in arbitrary order
        for (Map.Entry<E, Double> entry : elementsAndPriorities.entrySet()) {
            elements.add(new PriorityNode<>(entry.getKey(), entry.getValue()));
        }
        // Build the index map
        for (int i = 1; i < elements.size(); i++) {
            elementsToIndex.put(elements.get(i).getElement(), i);
        }
        // Bottom-up heapify: sink every internal node from the middle down to the root
        for (int i = size() / 2; i >= 1; i--) {
            sink(i);
        }
        // throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void add(E element, double priority) {
        if (contains(element)) {
            throw new IllegalArgumentException("Already contains " + element);
        }
        // TODO: Replace with your code
        elements.add(new PriorityNode<>(element, priority));
        int index = elements.size() - 1;
        elementsToIndex.put(element, index);
        swim(index);
        // throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public boolean contains(E element) {
        // TODO: Replace with your code
        return elementsToIndex.containsKey(element);
        // throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public double getPriority(E element) {
        // TODO: Replace with your code
        if (!contains(element)) {
            throw new NoSuchElementException("PQ does not contain element");
        }
        return elements.get(elementsToIndex.get(element)).getPriority();
        // throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public E peekMin() {
        if (isEmpty()) {
            throw new NoSuchElementException("PQ is empty");
        }
        // TODO: Replace with your code
        return elements.get(1).getElement();
        // throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public E removeMin() {
        if (isEmpty()) {
            throw new NoSuchElementException("PQ is empty");
        }
        // TODO: Replace with your code
        E min = elements.get(1).getElement();
        // Swap root with last element, remove last, then sink the new root
        int lastIndex = elements.size() - 1;
        swap(1, lastIndex);
        elements.remove(lastIndex);
        elementsToIndex.remove(min);
        if (!isEmpty()) {
            sink(1);
        }
        return min;
        // throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void changePriority(E element, double priority) {
        if (!contains(element)) {
            throw new NoSuchElementException("PQ does not contain " + element);
        }
        // TODO: Replace with your code
        int index = elementsToIndex.get(element);
        double oldPriority = elements.get(index).getPriority();
        elements.get(index).setPriority(priority);
        // Swim if priority decreased, sink if it increased
        if (priority < oldPriority) {
            swim(index);
        } else {
            sink(index);
        }
        // throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public int size() {
        // TODO: Replace with your code
        return elements.size() - 1;
        // throw new UnsupportedOperationException("Not implemented yet");
    }


        /**
     * Swims the node at the given index upward until heap order is restored.
     * A node swims up when its priority is less than its parent's priority.
     */
    private void swim(int index) {
        while (index > 1 && isHigherPriority(index, index / 2)) {
            swap(index, index / 2);
            index = index / 2;
        }
    }
 
    /**
     * Sinks the node at the given index downward until heap order is restored.
     * A node sinks down when its priority is greater than one of its children's priorities.
     */
    private void sink(int index) {
        while (2 * index <= size()) {
            int smallerChild = 2 * index; // start with left child
            // Check if right child exists and has a smaller priority
            if (smallerChild < size() && isHigherPriority(smallerChild + 1, smallerChild)) {
                smallerChild++;
            }
            // If the node is already <= its smallest child, heap order is restored
            if (!isHigherPriority(smallerChild, index)) {
                break;
            }
            swap(index, smallerChild);
            index = smallerChild;
        }
    }
 
    /**
     * Returns true if the node at index {@code a} has a strictly lower priority value
     * than the node at index {@code b} (i.e., {@code a} should be higher in a min-heap).
     */
    private boolean isHigherPriority(int a, int b) {
        return elements.get(a).getPriority() < elements.get(b).getPriority();
    }
 
    /**
     * Swaps the nodes at indices {@code a} and {@code b}, and updates {@code elementsToIndex} accordingly.
     */
    private void swap(int a, int b) {
        PriorityNode<E> nodeA = elements.get(a);
        PriorityNode<E> nodeB = elements.get(b);
        elements.set(a, nodeB);
        elements.set(b, nodeA);
        elementsToIndex.put(nodeA.getElement(), b);
        elementsToIndex.put(nodeB.getElement(), a);
    }
    

    @Override
    public String toString() {
        return elements + ", " + elementsToIndex;
    }
}
