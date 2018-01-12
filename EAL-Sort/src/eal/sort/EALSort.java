/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eal.sort;

import eal.methods.AFC;
import eal.methods.Clu;
import eal.methods.RDS;
import eal.methods.RDBS;
import eal.methods.Rand;
import eal.utils.ReadProperties;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import weka.core.Instances;
import eal.utils.IO;

/**
 *
 * @author guilherme
 */
public class EALSort {

    /**
     * @param args the command line arguments
     */
    private static String[] sort;
    private static int xNumClasses;
    
    public static void main(String[] args) throws IOException, Exception {
        
        ReadProperties rp = new ReadProperties();
        sort = rp.getSort();
        xNumClasses = rp.getxNumClasses();
        
        String basesSavePath = rp.getBasesSavePath();
        
        File[] filesZ2i = IO.getFiles("_z2i.arff", basesSavePath);
        File[] filesZ2ii = IO.getFiles("_z2ii.arff", basesSavePath);
        
        for (File file : filesZ2i) {
            System.err.print("Início da organização Z2i... ");
            sort(file, 0);
            System.err.println("Fim");
        }
        
        for (File file : filesZ2ii) {
            System.err.print("Início da organização Z2ii... ");
            sort(file, 1);
            System.err.println("Fim");
        }
        
    }

    private static void sort(File _file, int labOrUnlab) throws Exception {
        
        Instances file = IO.open(_file.getAbsolutePath());
        file.setClassIndex(file.numAttributes() - 1);
        
        
        
        for (String method : sort) {
            
            switch(method){
                case "AFC":
                    new AFC(file, file.numClasses() * xNumClasses,
                            _file.getName().split(".arff")[0], "AFC");
                    break;
                case "Clu":
                    new Clu(file, file.numClasses() * xNumClasses,
                            _file.getName().split(".arff")[0], "Clu");
                    break;
                case "RDBS":
                    new RDBS(file, file.numClasses() * xNumClasses,
                            _file.getName().split(".arff")[0], "RDBS");
                    break;
                case "RDS":
                    new RDS(file, file.numClasses() * xNumClasses,
                            _file.getName().split(".arff")[0], "RDS");
                    break;
                case "Rand":
                    new Rand(file, _file.getName().split(".arff")[0], "Rand");
                    break;
            }
        }
    }
    
}
