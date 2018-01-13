/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eal.supervised;

import eal.utils.IO;
import java.io.File;
import java.io.IOException;
import weka.classifiers.Classifier;
import weka.classifiers.functions.SMO;
import weka.core.Instances;

/**
 *
 * @author guilherme
 */
public class SVM extends Supervised{

    public SVM(Instances z2i, Instances z3) throws Exception {
        super(z2i, z3);
    }
    
    @Override
    public void train() throws Exception{
        classifier = new SMO();
        classifier.buildClassifier(z2i);
    }

    @Override
    public void saveAccuracy() throws IOException {
        
        //System.out.println(acc);
//        
//        String savePath = System.getProperty("user.dir").
//                    concat(File.separator).concat("results").
//                    concat(File.separator).concat(fileName).concat("_").concat(".txt");
//        
//        IO.save(String.valueOf(acc), path);
    }
    
    
    
}
