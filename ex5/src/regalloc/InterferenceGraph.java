package regalloc;

import java.util.*;
import temp.Temp;
import ir.IrCommand;

public class InterferenceGraph {
    public Set<Temp> nodes;
    public Map<Temp, Set<Temp>> edges;

    public InterferenceGraph() {
        nodes = new HashSet<>();
        edges = new HashMap<>();
    }

    public void addNode(Temp t) {
        if (!nodes.contains(t)) {
            nodes.add(t);
            edges.put(t, new HashSet<>());
        }
    }

    public void addEdge(Temp t1, Temp t2) {
        if (t1 == null || t2 == null || t1.equals(t2)) return;
        addNode(t1);
        addNode(t2);
        edges.get(t1).add(t2);
        edges.get(t2).add(t1);
    }

    public void build(LivenessAnalysis liveness, cfg.CFG cfg) {
        int n = cfg.size();
        
        // add nodes
        for (int i = 0; i < n; i++) {
            IrCommand cmd = cfg.getCommand(i);
            if (cmd.getDefinedTemps() != null) {
                for (Temp t : cmd.getDefinedTemps()) addNode(t);
            }
            if (cmd.getUsedTemps() != null) {
                for (Temp t : cmd.getUsedTemps()) addNode(t);
            }
        }

        // build edges
        for (int i = 0; i < n; i++) {
            Set<Temp> active = new HashSet<>();
            active.addAll(liveness.liveIn.get(i));
            active.addAll(liveness.liveOut.get(i));
            
            IrCommand cmd = cfg.getCommand(i);
            if (cmd.getDefinedTemps() != null) active.addAll(cmd.getDefinedTemps());
            if (cmd.getUsedTemps() != null) active.addAll(cmd.getUsedTemps());
            
            List<Temp> activeList = new ArrayList<>(active);
            for (int j = 0; j < activeList.size(); j++) {
                for (int k = j + 1; k < activeList.size(); k++) {
                    addEdge(activeList.get(j), activeList.get(k));
                }
            }
        }
    }
}
