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
        
        // Add all temps to graph
        for (int i = 0; i < n; i++) {
            IrCommand cmd = cfg.getCommand(i);
            if (cmd.getDefinedTemps() != null) {
                for (Temp t : cmd.getDefinedTemps()) addNode(t);
            }
            if (cmd.getUsedTemps() != null) {
                for (Temp t : cmd.getUsedTemps()) addNode(t);
            }
        }

        // Build edges
        for (int i = 0; i < n; i++) {
            IrCommand cmd = cfg.getCommand(i);
            Set<Temp> liveOut = liveness.liveOut.get(i);
            List<Temp> defs = cmd.getDefinedTemps();
            
            if (defs != null && !defs.isEmpty() && liveOut != null) {
                for (Temp d : defs) {
                    for (Temp l : liveOut) {
                        if (!d.equals(l)) {
                            addEdge(d, l);
                        }
                    }
                }
            }
        }
    }
}
