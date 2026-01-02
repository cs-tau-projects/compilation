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

/**
 * Dataflow state for "may be uninitialized" analysis.
 * 
 * Tracks a set of variables that may be uninitialized at a given program point.
 * This is a "may" analysis, so join is set union.
 */
public class UninitializedVarState implements DataflowState<UninitializedVarState>
{
    /****************/
    /* DATA MEMBERS */
    /****************/
    
    /** Set of variables that may be uninitialized */
    private Set<VarId> uninitializedVars;
    
    /******************/
    /* CONSTRUCTOR(S) */
    /******************/
    
    /** Create an empty state (bottom element) */
    public UninitializedVarState()
    {
        this.uninitializedVars = new HashSet<>();
    }
    
    /** Create a state with the given set of uninitialized variables */
    public UninitializedVarState(Set<VarId> uninitializedVars)
    {
        this.uninitializedVars = new HashSet<>(uninitializedVars);
    }
    
    /****************************************/
    /* DataflowState INTERFACE METHODS     */
    /****************************************/
    
    /**
     * Join this state with another (set union for "may" analysis).
     * If a variable is possibly uninitialized on ANY path, it's in the result.
     */
    @Override
    public void join(UninitializedVarState other)
    {
        this.uninitializedVars.addAll(other.uninitializedVars);
    }
    
    /**
     * Create an independent copy of this state.
     */
    @Override
    public UninitializedVarState copy()
    {
        return new UninitializedVarState(this.uninitializedVars);
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
        return this.uninitializedVars.equals(other.uninitializedVars);
    }
    
    @Override
    public int hashCode()
    {
        return uninitializedVars.hashCode();
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
    
    @Override
    public String toString()
    {
        return "UninitializedVarState{" + uninitializedVars + "}";
    }
}

