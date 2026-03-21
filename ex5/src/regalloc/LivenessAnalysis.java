package regalloc;

import java.util.*;
import ir.IrCommand;
import temp.Temp;
import cfg.CFG;

public class LivenessAnalysis {
    private CFG cfg;
    public Map<Integer, Set<Temp>> liveIn;
    public Map<Integer, Set<Temp>> liveOut;

    public LivenessAnalysis(CFG cfg) {
        this.cfg = cfg;
        this.liveIn = new HashMap<>();
        this.liveOut = new HashMap<>();
        
        int n = cfg.size();
        for (int i = 0; i < n; i++) {
            liveIn.put(i, new HashSet<>());
            liveOut.put(i, new HashSet<>());
        }
    }

    public void analyze() {
        boolean changed = true;
        int n = cfg.size();
        
        while (changed) {
            changed = false;
            
            // Backward traversal is usually faster for liveness
            for (int i = n - 1; i >= 0; i--) {
                IrCommand cmd = cfg.getCommand(i);
                
                Set<Temp> oldIn = new HashSet<>(liveIn.get(i));
                Set<Temp> oldOut = new HashSet<>(liveOut.get(i));
                
                // OUT[n] = U (IN[s] for s in succ[n])
                Set<Temp> newOut = new HashSet<>();
                for (int succ : cfg.getSuccessors(i)) {
                    newOut.addAll(liveIn.get(succ));
                }
                
                // IN[n] = USE[n] U (OUT[n] - DEF[n])
                Set<Temp> newIn = new HashSet<>(newOut);
                List<Temp> defs = cmd.getDefinedTemps();
                if (defs != null) newIn.removeAll(defs);
                
                List<Temp> uses = cmd.getUsedTemps();
                if (uses != null) newIn.addAll(uses);
                
                liveOut.put(i, newOut);
                liveIn.put(i, newIn);
                
                if (!oldIn.equals(newIn) || !oldOut.equals(newOut)) {
                    changed = true;
                }
            }
        }
    }
}
