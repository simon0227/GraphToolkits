package com.org.shark.graphtoolkits.metrics;

import com.org.shark.graphtoolkits.GenericGraphTool;
import com.org.shark.graphtoolkits.graph.Edge;
import com.org.shark.graphtoolkits.graph.Graph;
import com.org.shark.graphtoolkits.graph.Vertex;
import org.apache.commons.cli.CommandLine;

import com.org.shark.graphtoolkits.utils.GraphAnalyticTool;

import java.util.*;

/**
 * Created by yxshao on 10/5/15.
 */

@GraphAnalyticTool(
        name = "Graph Component Statistics",
        description = "Counting the Degree. format: (#vid degrees bigD smallD equalD bigL smallL)"
)
public class ComponentStatistics implements GenericGraphTool {

    private Graph graphData;
    private double threshold;

    public void run(CommandLine cmd) {
        graphData = new Graph(cmd.getOptionValue("i"));
        if(cmd.hasOption("th")) {
           this.threshold = Double.valueOf(cmd.getOptionValue("th"));
        }
        else {
           this.threshold = 20; //default is 20 matches
        }
        findComponents();
    }

    public boolean verifyParameters(CommandLine cmd) {
        return true;
    }

    private void findComponents() {
        HashMap<Integer, Vertex> vertices = graphData.getVertexSet();
        Set<Integer> visited = new HashSet<Integer>();

        Iterator<Integer> iter = vertices.keySet().iterator();
        int curCCId = 0;
        int singlePoint = 0;
        while(iter.hasNext()) {
            Integer cur = iter.next();
            if(visited.contains(cur)) {
               continue;
            }
            int ccSize = searchSingleComponent(cur, visited, vertices);
            if(ccSize > 1) {
                curCCId++;
                System.out.println("CurCCId=" + curCCId + ": size=" + ccSize);
            }
            else {
               singlePoint++;
            }
        }
        System.out.println("SinglePoint=" + singlePoint);
    }

    /**
     * search using BFS, the simplest solution.
     * -- threshold rules:
     *    1. the vertex weight (matches) greater than th;
     *    2. the edge weight should be greater than the average;
     * @param startId
     * @param visited
     * @return
     */
    private int searchSingleComponent(int startId, Set<Integer> visited, HashMap<Integer, Vertex> vertexSet) {
        Queue<Integer> queue = new LinkedList<Integer>();
        int size = 1;
        queue.add(startId);
        visited.add(startId);
        if(vertexSet.get(startId).getWeight() < this.threshold) {
            return size;
        }
        while(!queue.isEmpty()) {
           int vid = queue.peek();
            queue.remove();
            Vertex vertex = vertexSet.get(vid);
            ArrayList<Edge> nbrs = graphData.getNeighbors(vid);
            double average_weight = vertex.getWeight() / nbrs.size();
            for(int idx = 0; idx < nbrs.size(); idx++) {
                Edge e = nbrs.get(idx);
                //filter by visited or the weight is below threshold
                if(visited.contains(e.getId()) ||
                        e.getWeight() < average_weight || //filter by average weight under random model;
                        vertexSet.get(e.getId()).getWeight() < this.threshold) //filter by vertex threshold;
                    continue;
                visited.add(e.getId());
                queue.add(e.getId());
                size++;
            }
        }
        return size;
    }
}
