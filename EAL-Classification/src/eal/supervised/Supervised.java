/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eal.supervised;

import java.io.IOException;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;

/**
 *
 * @author guilherme
 */
public class Supervised implements ISupervised{
    
    protected final Instances z2i;
    protected final Instances z3;
    protected Classifier classifier;
    protected double acc;

    public Supervised(Instances z2i, Instances z3) throws Exception {
        this.z2i = z2i;
        this.z3 = z3;
        
        makeItHappen();
    }
    
    @Override
    public void makeItHappen() throws Exception{
        train();
        classify();
        saveAccuracy();
    }
    
    @Override
    public void train() throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void classify() throws Exception{
        Evaluation eval = new Evaluation(z2i);
        eval.evaluateModel(classifier, z3);
        acc = eval.pctCorrect();
    }

    @Override
    public void saveAccuracy() throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
