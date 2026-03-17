/***********/
/* PACKAGE */
/***********/
package cfg;

/*******************/
/* GENERAL IMPORTS */
/*******************/
import java.util.*;

/*******************/
/* PROJECT IMPORTS */
/*******************/
import ir.*;

/**
 * Control Flow Graph (CFG) for dataflow analysis.
 * 
 * Each node in the CFG corresponds to an IR command.
 * Edges represent possible control flow between commands.
 */
public class CFG
{
    /****************/
    /* DATA MEMBERS */
    /****************/
    
    /** The list of IR commands (nodes in the CFG) */
    private List<IrCommand> commands;
    
    /** Successors for each node (by index) */
    private List<Set<Integer>> successors;
    
    /** Predecessors for each node (by index) */
    private List<Set<Integer>> predecessors;
    
    /** Map from label names to their command indices */
    private Map<String, Integer> labelToIndex;
    
    /******************/
    /* CONSTRUCTOR(S) */
    /******************/
    public CFG(List<IrCommand> commands)
    {
        this.commands = commands;
        this.successors = new ArrayList<>();
        this.predecessors = new ArrayList<>();
        this.labelToIndex = new HashMap<>();
        
        // Initialize empty sets for each node
        for (int i = 0; i < commands.size(); i++)
        {
            successors.add(new HashSet<>());
            predecessors.add(new HashSet<>());
        }
        
        // Build the CFG
        buildLabelMap();
        buildEdges();
    }
    
    /****************************************/
    /* Build map from label names to indices */
    /****************************************/
    private void buildLabelMap()
    {
        for (int i = 0; i < commands.size(); i++)
        {
            IrCommand cmd = commands.get(i);
            if (cmd instanceof IrCommandLabel)
            {
                String labelName = ((IrCommandLabel) cmd).labelName;
                labelToIndex.put(labelName, i);
            }
        }
    }
    
    /****************************************/
    /* Build edges based on control flow   */
    /****************************************/
    private void buildEdges()
    {
        for (int i = 0; i < commands.size(); i++)
        {
            IrCommand cmd = commands.get(i);
            
            if (cmd instanceof IrCommandJumpLabel)
            {
                // Unconditional jump: edge to target label only
                String targetLabel = ((IrCommandJumpLabel) cmd).labelName;
                Integer targetIndex = labelToIndex.get(targetLabel);
                if (targetIndex != null)
                {
                    addEdge(i, targetIndex);
                }
                // No fall-through edge for unconditional jumps
            }
            else if (cmd instanceof IrCommandJumpIfEqToZero)
            {
                // Conditional jump: edge to target label AND fall-through
                String targetLabel = ((IrCommandJumpIfEqToZero) cmd).labelName;
                Integer targetIndex = labelToIndex.get(targetLabel);
                if (targetIndex != null)
                {
                    addEdge(i, targetIndex);
                }
                // Fall-through edge to next command
                if (i + 1 < commands.size())
                {
                    addEdge(i, i + 1);
                }
            }
            else
            {
                // Sequential command: edge to next command
                if (i + 1 < commands.size())
                {
                    addEdge(i, i + 1);
                }
            }
        }
    }
    
    /****************************************/
    /* Add an edge from src to dst         */
    /****************************************/
    private void addEdge(int src, int dst)
    {
        successors.get(src).add(dst);
        predecessors.get(dst).add(src);
    }
    
    /****************************************/
    /* PUBLIC ACCESSORS                    */
    /****************************************/
    
    /** Get the number of nodes in the CFG */
    public int size()
    {
        return commands.size();
    }
    
    /** Get the IR command at a given index */
    public IrCommand getCommand(int index)
    {
        return commands.get(index);
    }
    
    /** Get all IR commands */
    public List<IrCommand> getCommands()
    {
        return commands;
    }
    
    /** Get successors of a node */
    public Set<Integer> getSuccessors(int index)
    {
        return successors.get(index);
    }
    
    /** Get predecessors of a node */
    public Set<Integer> getPredecessors(int index)
    {
        return predecessors.get(index);
    }

    /****************************************/
    /* DEBUG: Print the CFG structure      */
    /****************************************/
    public void printCFG()
    {
        System.out.println("=== Control Flow Graph ===");
        System.out.println("Number of nodes: " + commands.size());
        System.out.println();

        for (int i = 0; i < commands.size(); i++)
        {
            IrCommand cmd = commands.get(i);
            String cmdType = cmd.getClass().getSimpleName();
            String cmdInfo = getCmdInfo(cmd);

            System.out.printf("[%d] %s%s\n", i, cmdType, cmdInfo);
            System.out.printf("    Predecessors: %s\n", predecessors.get(i));
            System.out.printf("    Successors:   %s\n", successors.get(i));
        }
        System.out.println("=== End CFG ===");
    }

    /****************************************/
    /* Get info string for a command       */
    /****************************************/
    private String getCmdInfo(IrCommand cmd)
    {
        if (cmd instanceof IrCommandLabel)
            return " (" + ((IrCommandLabel) cmd).labelName + ")";
        if (cmd instanceof IrCommandJumpLabel)
            return " -> " + ((IrCommandJumpLabel) cmd).labelName;
        if (cmd instanceof IrCommandJumpIfEqToZero)
            return " -> " + ((IrCommandJumpIfEqToZero) cmd).labelName + " (if zero)";
        if (cmd instanceof IrCommandAllocate)
            return " (" + ((IrCommandAllocate) cmd).varId.name + ")";
        if (cmd instanceof IrCommandLoad)
            return " (" + ((IrCommandLoad) cmd).varId.name + ")";
        if (cmd instanceof IrCommandStore)
            return " (" + ((IrCommandStore) cmd).varId.name + ")";
        return "";
    }
}

