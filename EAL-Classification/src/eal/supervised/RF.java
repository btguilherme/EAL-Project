/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eal.supervised;

import java.io.IOException;
import weka.classifiers.Classifier;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;

/**
 *
 * @author guilherme
 */
public class RF extends Supervised{
    
    public RF(Instances z2i, Instances z3) throws Exception {
        super(z2i, z3);
    }

    @Override
    public Classifier train() throws Exception {
        classifier = new RandomForest();
        classifier.buildClassifier(z2i);
        
        return classifier;
    }

    @Override
    public void saveAccuracy() throws IOException{
        System.out.println(acc);
    }
    
}
