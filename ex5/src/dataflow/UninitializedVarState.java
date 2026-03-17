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
import ir.VarId;
import temp.Temp;

/**
 * Dataflow state for "may be uninitialized" analysis.
 *
 * Tracks a set of variables that may be uninitialized at a given program point.
 * Also tracks temporaries that are "tainted" (hold values from uninitialized variables).
 * This is a "may" analysis, so join is set union.
 */
public class UninitializedVarState implements DataflowState<UninitializedVarState>
{
    /****************/
    /* DATA MEMBERS */
    /****************/

    /** Set of variables that may be uninitialized */
    private Set<VarId> uninitializedVars;

    /** Set of temporaries that hold values from uninitialized sources */
    private Set<Integer> taintedTemps;

    /******************/
    /* CONSTRUCTOR(S) */
    /******************/

    /** Create an empty state (bottom element) */
    public UninitializedVarState()
    {
        this.uninitializedVars = new HashSet<>();
        this.taintedTemps = new HashSet<>();
    }

    /** Create a state with the given sets */
    public UninitializedVarState(Set<VarId> uninitializedVars, Set<Integer> taintedTemps)
    {
        this.uninitializedVars = new HashSet<>(uninitializedVars);
        this.taintedTemps = new HashSet<>(taintedTemps);
    }
    
    /****************************************/
    /* DataflowState INTERFACE METHODS     */
    /****************************************/
    
    /**
     * Join this state with another (set union for "may" analysis).
     * If a variable is possibly uninitialized on ANY path, it's in the result.
     * Same for tainted temporaries.
     */
    @Override
    public void join(UninitializedVarState other)
    {
        this.uninitializedVars.addAll(other.uninitializedVars);
        this.taintedTemps.addAll(other.taintedTemps);
    }

    /**
     * Create an independent copy of this state.
     */
    @Override
    public UninitializedVarState copy()
    {
        return new UninitializedVarState(this.uninitializedVars, this.taintedTemps);
    }

    /**
     * Check equality for fixed-point detection.
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        UninitializedVarState other = (UninitializedVarState) obj;
        return this.uninitializedVars.equals(other.uninitializedVars)
            && this.taintedTemps.equals(other.taintedTemps);
    }

    @Override
    public int hashCode()
    {
        return 31 * uninitializedVars.hashCode() + taintedTemps.hashCode();
    }
    
    /****************************************/
    /* STATE MANIPULATION METHODS          */
    /****************************************/
    
    /** Add a variable to the uninitialized set (gen) */
    public void addUninitialized(VarId var)
    {
        uninitializedVars.add(var);
    }
    
    /** Remove a variable from the uninitialized set (kill) */
    public void removeUninitialized(VarId var)
    {
        uninitializedVars.remove(var);
    }
    
    /** Check if a variable is possibly uninitialized */
    public boolean isUninitialized(VarId var)
    {
        return uninitializedVars.contains(var);
    }
    
    /** Get the set of uninitialized variables (read-only) */
    public Set<VarId> getUninitializedVars()
    {
        return Collections.unmodifiableSet(uninitializedVars);
    }

    /****************************************/
    /* TAINTED TEMP MANIPULATION METHODS   */
    /****************************************/

    /** Mark a temporary as tainted (holds uninitialized value) */
    public void addTainted(Temp t)
    {
        taintedTemps.add(t.getSerialNumber());
    }

    /** Mark a temporary as clean (holds initialized value) */
    public void removeTainted(Temp t)
    {
        taintedTemps.remove(t.getSerialNumber());
    }

    /** Check if a temporary is tainted */
    public boolean isTainted(Temp t)
    {
        return taintedTemps.contains(t.getSerialNumber());
    }

    @Override
    public String toString()
    {
        return "UninitializedVarState{vars=" + uninitializedVars + ", temps=" + taintedTemps + "}";
    }
}

