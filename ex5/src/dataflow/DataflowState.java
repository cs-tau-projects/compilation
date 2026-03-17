/***********/
/* PACKAGE */
/***********/
package dataflow;

/**
 * Interface representing a dataflow lattice value.
 * 
 * Supports:
 * - join: combining states from multiple predecessors (for "may" analysis, this is union)
 * - copy: creating an independent copy of the state
 * - equals: checking if two states are the same (for fixed-point detection)
 * 
 * @param <T> The concrete type of the dataflow state
 */
public interface DataflowState<T extends DataflowState<T>>
{
    /**
     * Join this state with another state (in-place modification).
     * For "may" analysis: union (if ANY path has property, result has property)
     * For "must" analysis: intersection (if ALL paths have property, result has property)
     * 
     * @param other The state to join with
     */
    void join(T other);
    
    /**
     * Create an independent copy of this state.
     * 
     * @return A new state with the same values
     */
    T copy();
    
    /**
     * Check if this state equals another state.
     * Used for fixed-point detection.
     * 
     * @param other The state to compare with
     * @return true if the states are equal
     */
    boolean equals(Object other);
    
    /**
     * Hash code for use in collections.
     */
    int hashCode();
}

