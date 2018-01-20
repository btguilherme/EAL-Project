/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eal.unsupervised;

import java.io.IOException;
import weka.classifiers.Classifier;
import weka.classifiers.CollectiveEvaluation;
import weka.core.Instances;

/**
 *
 * @author guilherme
 */
public class Unsupervised implements IUnsupervised{
    
    protected final Instances z2i;
    protected final Instances z2ii;
    protected final Instances z3;
    protected Classifier classifier;
    protected double acc;
    
    public Unsupervised(Instances z2i, Instances z2ii, Instances z3) throws Exception{
        this.z2i = z2i;
        this.z2ii = z2ii;
        this.z3 = z3;
        
        makeItHappen();
    }

    @Override
    public void makeItHappen() throws Exception {
        train();
        classify();
        saveAccuracy();
    }

    @Override
    public void train() throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void classify() throws Exception {
        CollectiveEvaluation eval = new CollectiveEvaluation(z2i);
        eval.evaluateModel(classifier, z3);
        acc = eval.pctCorrect();
    }

    @Override
    public void saveAccuracy() throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public Classifier getClassifier() {
        return classifier;
    }

    public double getAcc() {
        return acc;
    }    
    
}
