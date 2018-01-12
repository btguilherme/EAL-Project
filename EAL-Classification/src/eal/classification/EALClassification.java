/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eal.classification;

import eal.supervised.RF;
import eal.supervised.SVM;
import eal.utils.IO;
import eal.utils.ReadProperties;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import weka.core.Instances;

/**
 *
 * @author guilherme
 */
public class EALClassification {

    private static String[] classifiers;
    private static int xNumClasses;
    private static boolean outOfSamples;
    private static Instances _z2i;
    private static Instances _z2ii;
    private static Instances z3;

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException, Exception {
        
        ReadProperties rp = new ReadProperties();
        classifiers = rp.getClassifiers();
        xNumClasses = rp.getxNumClasses();
        String basesSavePath = rp.getBasesSavePath();
        String fileName = rp.getArffFile().split(".arff")[0];
        String splitsSortedPath = rp.getSplitsSorted();
        
        File[] files = IO.getFiles(".arff", splitsSortedPath);
        Set<String> uuids = new HashSet<>();
        
        for (File file : files) {
            uuids.add(file.getName().split("@")[0]);
        }
        

        fazer o for abaixo para outros métodos RDS, RDBS, etc

        
        for (String uuid : uuids) {
            for (int i = 0; i < files.length; i++) {
                
                if(files[i].getName().startsWith(uuid) && files[i].getName().
                       contains("AFC") && files[i].getName().contains("_z2i_")){
                    
                    loadsFiles(files[i], basesSavePath);
                    
                    Instances z2i = new Instances(_z2i);
                    z2i.delete();
                    Instances z2ii = new Instances(_z2ii);
                    z2ii.delete();
                    
                    do {
                        z2i = selectSamples(z2i, _z2i, _z2i.numClasses() * xNumClasses);
                        //z2ii = selectSamples(z2ii, _z2ii, _z2i.numClasses());
                        
                        makesClassification(z2i);
                        
                    } while (!outOfSamples);  
                }
            }   
        }
    }

    private static Instances selectSamples(Instances ret, Instances file, int numSamples) {

        outOfSamples = false;

        if(numSamples > file.numInstances()){
            numSamples = file.numInstances();
            outOfSamples = true;
        }

        for (int i = 0; i < numSamples; i++)
            ret.add(file.instance(i));

        for (int i = 0; i < numSamples; i++)
            file.delete(0);

        
        return ret;   
    }

    private static void loadsFiles(File file, String basesSavePath) throws Exception {
        _z2i = IO.open(file.getAbsolutePath());
        _z2i.setClassIndex(_z2i.numAttributes() - 1);
        _z2ii = IO.open(file.getAbsolutePath().replace("_z2i_", "_z2ii_"));
        _z2ii.setClassIndex(_z2ii.numAttributes() - 1);
        z3 = IO.open(basesSavePath + file.getName().split("_z2")[0] + "_z3.arff");
        z3.setClassIndex(z3.numAttributes() - 1);
    }

    private static void makesClassification(Instances z2i) throws Exception {
        
        for (String classifier : classifiers) {
            switch (classifier) {
                case "SVM":
                    new SVM(z2i, z3);
                    break;
                case "RF":
                    new RF(z2i, z3);
                    break;
            }
        }
        
    }
    
}
