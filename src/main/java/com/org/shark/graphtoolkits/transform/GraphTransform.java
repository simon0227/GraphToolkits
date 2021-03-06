package com.org.shark.graphtoolkits.transform;

import com.org.shark.graphtoolkits.GenericGraphTool;
import com.org.shark.graphtoolkits.graph.Edge;
import com.org.shark.graphtoolkits.graph.Vertex;
import com.org.shark.graphtoolkits.utils.GraphAnalyticTool;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Pattern;

/**
 * Created by yxshao on 10/7/15.
 */

@GraphAnalyticTool(
        name = "Transform graphs",
        description = "Some basic graph operations."
)
public class GraphTransform implements GenericGraphTool {
    protected static final Pattern SEPERATOR =  Pattern.compile("[\t ]");

    HashMap<Integer, HashMap<Integer, Integer>> edgeWeights;

    public GraphTransform() {

    }

    @Override
    public void registerOptions(Options options) {

    }

    @Override
    public void run(CommandLine cmd) {
        String path = cmd.getOptionValue("i");
        String savePath = "weighted_graph.data";

        if(cmd.hasOption("op")) {
            savePath = cmd.getOptionValue("op");
        }

        graphEdgeToWeightGraph(path, savePath);
    }

    @Override
    public boolean verifyParameters(CommandLine cmd) {
        return true;
    }

    public void graphEdgeToWeightGraph(String path, String savePath) {
        try {
            System.out.println("in path="+path+" outPath=" +savePath);
            FileInputStream fin = new FileInputStream(path);
            BufferedReader fbr = new BufferedReader(new InputStreamReader(fin));
            String line;
            int lineNum = 0;
            edgeWeights = new HashMap<Integer, HashMap<Integer, Integer>>();
            while((line = fbr.readLine()) != null) {
                lineNum++;
                if (line.startsWith("#")) continue;
                String[] values = SEPERATOR.split(line);
                if (values.length < 2) {
                    System.out.println("Edge Format Required. Error Line: " + line + ". parsed value size = " + values.length);
                    continue;
                }

                for (int i = 0; i < values.length; i++) {
                    if (values[i] == null || values[i].length() == 0) {
                        System.out.println("Error line " + lineNum + ": " + line);
                    }
                }

                int sv = Integer.valueOf(values[0]);
                int ev = Integer.valueOf(values[1]);

                if (!edgeWeights.containsKey(sv)) {
                    edgeWeights.put(sv, new HashMap<Integer, Integer>());
                }
                HashMap<Integer, Integer> nbrs = edgeWeights.get(sv);
                if (!nbrs.containsKey(ev)) {
                    nbrs.put(ev, 1);
                } else {
                    nbrs.put(ev, 1 + nbrs.get(ev));
                }
            }
            saveGraph(savePath);
            fbr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveGraph(String savePath) {
        try{
            FileOutputStream fout = new FileOutputStream(savePath);
            BufferedWriter fwr = new BufferedWriter(new OutputStreamWriter(fout));
            for(int vid : edgeWeights.keySet()) {
                HashMap<Integer, Integer> nbrs = edgeWeights.get(vid);
                for(int tid : nbrs.keySet()) {
                    StringBuffer sb = new StringBuffer();
                    sb.append(vid);
                    sb.append(" ");
                    sb.append(tid);
                    sb.append(" ");
                    sb.append(nbrs.get(tid));
                    sb.append("\n");
                    fwr.write(sb.toString());

                }
            }
            fwr.flush();
            fwr.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
