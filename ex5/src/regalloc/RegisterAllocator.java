package regalloc;

import java.util.*;
import temp.Temp;

public class RegisterAllocator {
    private static final int K = 10;
    public Map<Temp, Integer> allocation; 

    public RegisterAllocator() {
        this.allocation = new HashMap<>();
    }

    public void allocate(InterferenceGraph graph) {
        Stack<Temp> stack = new Stack<>();

        // Setup working copies of graph nodes and edges
        Set<Temp> workingNodes = new HashSet<>(graph.nodes);
        Map<Temp, Set<Temp>> workingEdges = new HashMap<>();

        for (Temp t : graph.nodes) {
            workingEdges.put(t, new HashSet<>(graph.edges.get(t)));
        }

        // Simplify phase
        while (!workingNodes.isEmpty()) {
            Temp toRemove = null;

            // Find node with degree < K
            for (Temp t : workingNodes) {
                if (workingEdges.get(t).size() < K) {
                    toRemove = t;
                    break;
                }
            }

            if (toRemove == null) {
                // Cannot color graph without spilling
                throw new RuntimeException("Register Allocation Failed");
            }

            workingNodes.remove(toRemove);
            stack.push(toRemove);

            // Remove edges connected to `toRemove`
            for (Temp neighbor : workingEdges.get(toRemove)) {
                if (workingNodes.contains(neighbor)) {
                    workingEdges.get(neighbor).remove(toRemove);
                }
            }
        }

        // Select phase
        while (!stack.isEmpty()) {
            Temp t = stack.pop();

            // Available colors: 0 to 9
            boolean[] usedColors = new boolean[K];
            for (Temp neighbor : graph.edges.get(t)) {
                if (allocation.containsKey(neighbor)) {
                    usedColors[allocation.get(neighbor)] = true;
                }
            }

            // Find first available color
            int color = -1;
            for (int c = 0; c < K; c++) {
                if (!usedColors[c]) {
                    color = c;
                    break;
                }
            }

            if (color == -1) {
                throw new RuntimeException("Register Allocation Failed");
            }

            allocation.put(t, color);
        }
    }

    public Map<Temp, String> getRegisterMap() {
        Map<Temp, String> result = new HashMap<>();
        for (Map.Entry<Temp, Integer> entry : allocation.entrySet()) {
            result.put(entry.getKey(), "$t" + entry.getValue());
        }
        return result;
    }
}
