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
    
    /** Index of the "main" label in the IR (separates globals from main) */
    private int mainLabelIndex;
    
    /******************/
    /* CONSTRUCTOR(S) */
    /******************/
    public UninitializedVarAnalysis(CFG cfg)
    {
        super(cfg);
        this.uninitializedUses = new TreeSet<>(); // TreeSet for alphabetical order
        this.mainLabelIndex = findMainLabelIndex();
    }
    
    /****************************************/
    /* Find the index of the "main" label  */
    /****************************************/
    private int findMainLabelIndex()
    {
        List<IrCommand> commands = cfg.getCommands();
        for (int i = 0; i < commands.size(); i++)
        {
            IrCommand cmd = commands.get(i);
            if (cmd instanceof IrCommandLabel)
            {
                if ("main".equals(((IrCommandLabel) cmd).labelName))
                {
                    return i;
                }
            }
        }
        return 0; // Default to start if no main label found
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
     * Scan global variable declarations (before main) to find uninitialized globals.
     */
    @Override
    protected UninitializedVarState createBoundaryState()
    {
        UninitializedVarState state = new UninitializedVarState();
        
        // Process all commands before main to determine initial global state
        // We simulate the effect of global declarations
        List<IrCommand> commands = cfg.getCommands();
        for (int i = 0; i < mainLabelIndex; i++)
        {
            IrCommand cmd = commands.get(i);
            if (cmd instanceof IrCommandAllocate)
            {
                // Global variable declared - starts uninitialized
                state.addUninitialized(((IrCommandAllocate) cmd).varId);
            }
            else if (cmd instanceof IrCommandStore)
            {
                // Global variable initialized
                state.removeUninitialized(((IrCommandStore) cmd).varId);
            }
        }
        
        return state;
    }
    
    /**
     * Apply the transfer function for a single IR command.
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
        else if (cmd instanceof IrCommandStore)
        {
            // kill: variable becomes initialized
            VarId varId = ((IrCommandStore) cmd).varId;
            state.removeUninitialized(varId);
        }
        else if (cmd instanceof IrCommandLoad)
        {
            // Check if loading an uninitialized variable
            VarId varId = ((IrCommandLoad) cmd).varId;
            if (state.isUninitialized(varId))
            {
                // Record the variable name (not the full VarId)
                uninitializedUses.add(varId.name);
            }
        }
        // All other commands: no effect on uninitialized state
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
}

