/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eal.methods;

import eal.utils.IO;
import eal.utils.MapUtil;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import weka.core.EuclideanDistance;
import weka.core.Instances;

/**
 *
 * @author guilherme
 */
public class RDS extends AFC{
    
    protected Instances raizes;
    protected Instances[] listas;
    
    public RDS(Instances file, int nClusters, String fileName, String method) throws Exception{
        super(file, nClusters, fileName, method);
    }

    @Override
    public void makeItHappen() throws Exception {
        cluster();
        raizes = clusterer.getClusterCentroids();
        sort(file);
        save();
    }

    @Override
    public Instances sort(Instances amostrasDeFronteira) {
        
        Instances ret = new Instances(amostrasDeFronteira);
        ret.delete();
        
        listas = new Instances[nClusters];
        
        int[] assign = null;
        try {
            assign = clusterer.getAssignments();
        } catch (Exception ex) {
            Logger.getLogger(RDS.class.getName()).log(Level.SEVERE, null, ex);
        }

        for (int i = 0; i < raizes.numInstances(); i++) {
            
            Map<Integer, Double> instDistRel = new HashMap<>();
            int clusterNum = 0;
            try {
                clusterNum = clusterer.clusterInstance(raizes.instance(i));
            } catch (Exception ex) {
                Logger.getLogger(RDS.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            listas[clusterNum] = new Instances(file);
            listas[clusterNum].delete();
            
            for (int j = 0; j < assign.length; j++) {
                if(assign[j] == clusterNum){
                    EuclideanDistance ed = new EuclideanDistance(file);
                    double distance = ed.distance(raizes.instance(i), 
                            file.instance(j));
                    instDistRel.put(j, distance);
                }
            }
            
            Map<Integer, Double> _sorted = MapUtil.sortByValue(instDistRel);
            
            Set<Integer> keys = _sorted.keySet();
            
            for (Integer key : keys) {
                listas[clusterNum].add(file.instance(key));
            }
        }
        
        return ret;
    }
    
    @Override
    protected void save() throws IOException {
        
        String savePath;
        
        for (int i = 0; i < listas.length; i++) {
        
            savePath = System.getProperty("user.dir").concat(File.separator).
                    concat("arff-files-sorted").concat(File.separator).
                    concat(fileName).concat("_").concat(method).
                    concat("_lista_").concat(String.valueOf(i)).concat(".arff");
            
            IO.save(listas[i], savePath);   
        }
    }
    
    
}
