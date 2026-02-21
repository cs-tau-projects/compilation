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
 * Dataflow analysis to detect possibly uninitialized variable usage.
 * 
 * This is a forward "may" analysis:
 * - gen(Allocate x) = {x}  (new variable starts uninitialized)
 * - kill(Store x) = {x}    (variable becomes initialized)
 * - Load x with x in IN[n] = uninitialized use detected
 */
public class UninitializedVarAnalysis extends DataflowAnalysis<UninitializedVarState>
{
    /****************/
    /* DATA MEMBERS */
    /****************/
    
    /** Set of variable names that were used while possibly uninitialized */
    private Set<String> uninitializedUses;
    
    /******************/
    /* CONSTRUCTOR(S) */
    /******************/
    public UninitializedVarAnalysis(CFG cfg)
    {
        super(cfg);
        this.uninitializedUses = new TreeSet<>(); // TreeSet for alphabetical order
    }
    
    /****************************************/
    /* ABSTRACT METHOD IMPLEMENTATIONS     */
    /****************************************/
    
    /**
     * Create the initial (bottom) state: empty set.
     */
    @Override
    protected UninitializedVarState createInitialState()
    {
        return new UninitializedVarState();
    }
    
    /**
     * Create the boundary state for the entry node.
     * Starts with an empty state (no variables uninitialized yet).
     */
    @Override
    protected UninitializedVarState createBoundaryState()
    {
        return new UninitializedVarState();
    }
    
    /**
     * Apply the transfer function for a single IR command.
     * Tracks both uninitialized variables and tainted temporaries.
     */
    @Override
    protected void transfer(IrCommand cmd, UninitializedVarState state)
    {
        if (cmd instanceof IrCommandAllocate)
        {
            // gen: new variable starts uninitialized
            VarId varId = ((IrCommandAllocate) cmd).varId;
            state.addUninitialized(varId);
        }
        else if (cmd instanceof IrCommandLoad)
        {
            // Load dst <- var
            IrCommandLoad load = (IrCommandLoad) cmd;
            if (state.isUninitialized(load.varId))
            {
                // Report the uninitialized use
                uninitializedUses.add(load.varId.name);
                // Taint the destination temp
                state.addTainted(load.dst);
            }
            else
            {
                // Variable is initialized, so temp is clean
                state.removeTainted(load.dst);
            }
        }
        else if (cmd instanceof IrCommandStore)
        {
            // Store var <- src
            IrCommandStore store = (IrCommandStore) cmd;
            if (state.isTainted(store.src))
            {
                // Storing a tainted value - variable remains/becomes uninitialized
                state.addUninitialized(store.varId);
            }
            else
            {
                // Storing an initialized value - variable becomes initialized
                state.removeUninitialized(store.varId);
            }
        }
        else if (cmd instanceof IRcommandConstInt)
        {
            // ConstInt dst <- value: constants are always initialized
            IRcommandConstInt constInt = (IRcommandConstInt) cmd;
            state.removeTainted(constInt.t);
        }
        else if (cmd instanceof IrCommandBinopAddIntegers)
        {
            IrCommandBinopAddIntegers binop = (IrCommandBinopAddIntegers) cmd;
            transferBinop(state, binop.dst, binop.t1, binop.t2);
        }
        else if (cmd instanceof IrCommandBinopSubIntegers)
        {
            IrCommandBinopSubIntegers binop = (IrCommandBinopSubIntegers) cmd;
            transferBinop(state, binop.dst, binop.t1, binop.t2);
        }
        else if (cmd instanceof IrCommandBinopMulIntegers)
        {
            IrCommandBinopMulIntegers binop = (IrCommandBinopMulIntegers) cmd;
            transferBinop(state, binop.dst, binop.t1, binop.t2);
        }
        else if (cmd instanceof IrCommandBinopDivIntegers)
        {
            IrCommandBinopDivIntegers binop = (IrCommandBinopDivIntegers) cmd;
            transferBinop(state, binop.dst, binop.t1, binop.t2);
        }
        else if (cmd instanceof IrCommandBinopLtIntegers)
        {
            IrCommandBinopLtIntegers binop = (IrCommandBinopLtIntegers) cmd;
            transferBinop(state, binop.dst, binop.t1, binop.t2);
        }
        else if (cmd instanceof IrCommandBinopGtIntegers)
        {
            IrCommandBinopGtIntegers binop = (IrCommandBinopGtIntegers) cmd;
            transferBinop(state, binop.dst, binop.t1, binop.t2);
        }
        else if (cmd instanceof IrCommandBinopEqIntegers)
        {
            IrCommandBinopEqIntegers binop = (IrCommandBinopEqIntegers) cmd;
            transferBinop(state, binop.dst, binop.t1, binop.t2);
        }
        // Labels, jumps, PrintInt: no effect on uninitialized/tainted state
    }

    /**
     * Helper for binary operations: if either operand is tainted, result is tainted.
     */
    private void transferBinop(UninitializedVarState state, temp.Temp dst, temp.Temp t1, temp.Temp t2)
    {
        if (state.isTainted(t1) || state.isTainted(t2))
        {
            state.addTainted(dst);
        }
        else
        {
            state.removeTainted(dst);
        }
    }
    
    /****************************************/
    /* GET RESULTS                         */
    /****************************************/
    
    /**
     * Get the set of variable names that were used while possibly uninitialized.
     * The set is sorted alphabetically.
     */
    public Set<String> getUninitializedUses()
    {
        return uninitializedUses;
    }
    
    /**
     * Run the dataflow analysis using chaotic iteration (worklist algorithm).
     * After this method returns, inStates and outStates contain the fixed-point solution.
     */
    @Override
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
                UninitializedVarState newIn = createInitialState();
                for (int predIdx : preds)
                {
                    newIn.join(outStates.get(predIdx));
                }
                inStates.set(nodeIdx, newIn);
            }
            
            // Compute OUT[n] = transfer(n, IN[n])
            UninitializedVarState newOut = inStates.get(nodeIdx).copy();
            
            // Capture uninitialized uses during the transfer function
            // We need to do this carefully because we might visit nodes multiple times
            // and we don't want to report the same error multiple times if we were printing immediately,
            // but since we're collecting into a Set, it's fine.
            // However, we only want to collect errors from the FINAL fixed-point state.
            
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
        
        // After fixed point is reached, do one final pass to collect all uninitialized uses
        // based on the final IN states.
        uninitializedUses.clear(); // Clear any intermediate results
        for (int i = 0; i < n; i++)
        {
            // We need to run transfer one last time on the final IN state to catch uses
            // We use a copy so we don't modify the stored IN state (though it shouldn't matter at this point)
            UninitializedVarState state = inStates.get(i).copy();
            transfer(cfg.getCommand(i), state);
        }
    }
}
