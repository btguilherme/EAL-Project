/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eal.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

/**
 *
 * @author guilherme
 */
public class IO {
    
    public static Instances open(String path) throws Exception{
        ConverterUtils.DataSource source = 
                new ConverterUtils.DataSource(path);
        Instances data = source.getDataSet();
        
        return data;
    }
    
    public static void save(Instances data, String path) throws IOException{
        BufferedWriter writer = new BufferedWriter(
                           new FileWriter(path));
        writer.write(data.toString());
        writer.newLine();
        writer.flush();
        writer.close();
    }
    
    public static void save(String data, String path) throws IOException{
        BufferedWriter writer = new BufferedWriter(
                           new FileWriter(path));
        writer.write(data);
        writer.newLine();
        writer.flush();
        writer.close();
    }
    
    public static void saveConcat(String data, String path) throws IOException{
        BufferedWriter writer = new BufferedWriter(
                           new FileWriter(path, true));
        writer.write(data);
        writer.newLine();
        writer.flush();
        writer.close();
    }
    
    public static File[] getFiles(String ends, String basesSavePath) {
        File arffFiles = new File(basesSavePath);
        FileFilter filter = (File file) -> file.getName().endsWith(ends);
        return arffFiles.listFiles(filter);
    }
    
    
    //usará?????
    public static void saveIterations(Instances file, String fileName, 
            String method, int numSamples, Instances sorted) throws IOException{
        
        Instances toSave = new Instances(file);
        toSave.delete();

        int cont = 0;
        boolean outOfSamples = false;
        do{
            if(numSamples > sorted.numInstances()){
                numSamples = sorted.numInstances();
                outOfSamples = true;
            }

            for (int i = 0; i < numSamples; i++)
                toSave.add(sorted.instance(i));

            for (int i = 0; i < numSamples; i++)
                sorted.delete(0);

            String savePath = System.getProperty("user.dir").
                    concat(File.separator).concat("arff-files-sorted").
                    concat(File.separator).concat(fileName).concat("_").
                    concat(method).concat("_it").concat(String.valueOf(cont)).
                    concat(".arff");

            IO.save(toSave, savePath);

            cont++;
        } while (!outOfSamples);
    }
    
}
