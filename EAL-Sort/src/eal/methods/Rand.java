/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eal.methods;

import eal.utils.IO;
import java.io.File;
import java.io.IOException;
import weka.core.Debug;
import weka.core.Instances;

/**
 *
 * @author guilherme
 */
public class Rand {

    private final Instances file;
    private final String method;
    private final String fileName;
    
    public Rand(Instances file, String fileName, String method) throws IOException{
        this.file = file;
        this.fileName = fileName;
        this.method = method;
        makeItHappen();
    }
    
    protected void makeItHappen() throws IOException{
        file.randomize(new Debug.Random());
        save();
    }

    protected void save() throws IOException {
        
        String savePath = System.getProperty("user.dir").
                    concat(File.separator).concat("arff-files-sorted").
                    concat(File.separator).concat(fileName).concat("_").
                    concat(method).concat(".arff");
        
        IO.save(file, savePath);
        
    }
    
}
