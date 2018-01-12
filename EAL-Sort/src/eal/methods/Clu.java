/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eal.methods;

import eal.utils.MapUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import weka.core.Debug;
import weka.core.EuclideanDistance;
import weka.core.Instances;


/**
 *
 * @author guilherme
 */
public class Clu extends RDS{

    public Clu(Instances file, int nClusters, String fileName, String method)
            throws Exception {
        
        super(file, nClusters, fileName, method);
    }

    @Override
    public void makeItHappen() throws Exception {
        cluster();
        raizes = roots(clusterer.getClusterCentroids());
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
            Logger.getLogger(Clu.class.getName()).log(Level.SEVERE, null, ex);
        }

        for (int i = 0; i < raizes.numInstances(); i++) {
            
            int clusterNum = 0;
            try {
                clusterNum = clusterer.clusterInstance(raizes.instance(i));
            } catch (Exception ex) {
                Logger.getLogger(Clu.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            listas[clusterNum] = new Instances(file);
            listas[clusterNum].delete();
            
            Instances clusterSamples = new Instances(file);
            clusterSamples.delete();
            
            for (int j = 0; j < assign.length; j++) {
                if(assign[j] == clusterNum){
                    clusterSamples.add(file.instance(j));
                }
            }
            
            clusterSamples.randomize(new Debug.Random());
            
            listas[clusterNum] = clusterSamples;
        }
        
        return ret;
    }
    
    
    

    
    
}