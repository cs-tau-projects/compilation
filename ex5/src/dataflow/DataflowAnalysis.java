/***********/
/* PACKAGE */
/***********/
package dataflow;

/*******************/
/* GENERAL IMPORTS */
/*******************/
import java.util.*;

/*******************/
/* PROJECT IMPORTS */
/*******************/
import cfg.*;
import ir.*;

/**
 * Abstract base class for forward dataflow analysis using chaotic iteration.
 * 
 * Implements:
 * - createInitialState(): create the bottom element of the lattice
 * - createBoundaryState(): create the initial state for the entry node
 * - transfer(): apply the transfer function for a single IR command
 * 
 * @param <T> The concrete type of the dataflow state
 */
public abstract class DataflowAnalysis<T extends DataflowState<T>>
{
    /****************/
    /* DATA MEMBERS */
    /****************/
    
    /** The control flow graph */
    protected CFG cfg;
    
    /** IN state for each node (state at entry of node) */
    protected List<T> inStates;
    
    /** OUT state for each node (state at exit of node) */
    protected List<T> outStates;
    
    /******************/
    /* CONSTRUCTOR(S) */
    /******************/
    public DataflowAnalysis(CFG cfg)
    {
        this.cfg = cfg;
        this.inStates = new ArrayList<>();
        this.outStates = new ArrayList<>();
    }
    
    /****************************************/
    /* ABSTRACT METHODS - must be overridden */
    /****************************************/
    
    /**
     * Create the initial (bottom) state for the lattice.
     * For "may be uninitialized": empty set (no variables uninitialized)
     */
    protected abstract T createInitialState();
    
    /**
     * Create the boundary state for the entry node.
     * For "may be uninitialized": set of all uninitialized global variables
     */
    protected abstract T createBoundaryState();
    
    /**
     * Apply the transfer function for a single IR command.
     * Modifies the state in-place.
     * 
     * @param cmd The IR command
     * @param state The current state (will be modified)
     */
    protected abstract void transfer(IrCommand cmd, T state);
    
    /****************************************/
    /* RUN THE ANALYSIS                    */
    /****************************************/
    
    /**
     * Run the dataflow analysis using chaotic iteration (worklist algorithm).
     * After this method returns, inStates and outStates contain the fixed-point solution.
     */
    public void analyze()
    {
        int n = cfg.size();
        if (n == 0) return;
        
        // Initialize IN and OUT states for all nodes
        for (int i = 0; i < n; i++)
        {
            inStates.add(createInitialState());
            outStates.add(createInitialState());
        }
        
        // Set boundary condition for entry node (node 0)
        inStates.set(0, createBoundaryState());
        
        // Initialize worklist with all nodes
        Queue<Integer> worklist = new LinkedList<>();
        Set<Integer> inWorklist = new HashSet<>();
        for (int i = 0; i < n; i++)
        {
            worklist.add(i);
            inWorklist.add(i);
        }
        
        // Iterate until fixed point
        while (!worklist.isEmpty())
        {
            // Remove a node from the worklist
            int nodeIdx = worklist.poll();
            inWorklist.remove(nodeIdx);
            
            // Compute IN[n] = join of OUT[p] for all predecessors p
            // (except for entry node which keeps its boundary state)
            Set<Integer> preds = cfg.getPredecessors(nodeIdx);
            if (!preds.isEmpty())
            {
                T newIn = createInitialState();
                for (int predIdx : preds)
                {
                    newIn.join(outStates.get(predIdx));
                }
                inStates.set(nodeIdx, newIn);
            }
            
            // Compute OUT[n] = transfer(n, IN[n])
            T newOut = inStates.get(nodeIdx).copy();
            transfer(cfg.getCommand(nodeIdx), newOut);
            
            // If OUT changed, add successors to worklist
            if (!newOut.equals(outStates.get(nodeIdx)))
            {
                outStates.set(nodeIdx, newOut);
                for (int succIdx : cfg.getSuccessors(nodeIdx))
                {
                    if (!inWorklist.contains(succIdx))
                    {
                        worklist.add(succIdx);
                        inWorklist.add(succIdx);
                    }
                }
            }
        }
    }
    
    /****************************************/
    /* ACCESSORS                           */
    /****************************************/
    
    /** Get the IN state for a node */
    public T getInState(int nodeIdx)
    {
        return inStates.get(nodeIdx);
    }
    
    /** Get the OUT state for a node */
    public T getOutState(int nodeIdx)
    {
        return outStates.get(nodeIdx);
    }
}

