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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import weka.classifiers.Classifier;
import weka.core.Instances;

/**
 *
 * @author guilherme
 */
public class EALClassification {

    private static String[] classifiers;
    private static int xNumClasses;
    private static boolean outOfSamples;
    private static Instances z2iSingle;
    private static Instances z2iiSingle;
    private static Instances[] z2iMultiple;
    private static Instances[] z2iiMultiple;
    private static Instances z3;
    private static String basesSavePath;
    private static Classifier classificador;
    

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException, Exception {
        
        ReadProperties rp = new ReadProperties();
        classifiers = rp.getClassifiers();
        xNumClasses = rp.getxNumClasses();
        basesSavePath = rp.getBasesSavePath();
        String fileName = rp.getArffFile().split(".arff")[0];
        String splitsSortedPath = rp.getSplitsSorted();
        
        File[] files = IO.getFiles(".arff", splitsSortedPath);
        Set<String> uuids = new HashSet<>();
        
        for (File file : files) {
            uuids.add(file.getName().split("@")[0]);
        }

        
        for (String uuid : uuids) {
            
            for (int i = 0; i < files.length; i++) {
                
                if(files[i].getName().startsWith(uuid) && files[i].getName().
                       contains("AFC") && files[i].getName().contains("_z2i_")){
         
                    routineSingleList(files[i]);

                } else if(files[i].getName().startsWith(uuid) && files[i].getName().
                       contains("Rand") && files[i].getName().contains("_z2i_")){
                    
                    routineSingleList(files[i]);
                    
                } else if(files[i].getName().startsWith(uuid) && files[i].getName().
                       contains("RDS") && files[i].getName().contains("_z2i_")){
                    
                    routineMultipleLists(uuid, files);
                    
                } else if(files[i].getName().startsWith(uuid) && files[i].getName().
                       contains("RDBS") && files[i].getName().contains("_z2i_")){
                    
                    routineMultipleLists(uuid, files);
                    
                } else if(files[i].getName().startsWith(uuid) && files[i].getName().
                       contains("Clu") && files[i].getName().contains("_z2i_")){
                    
                    //No Clu, a seleção é aleatória
                    //Modificar a rotina abaixo para este caso
                    routineMultipleLists(uuid, files);
                    
                }
            }   
        }
    }
    
    private static Instances selectSamplesMultipleLists(Instances ret, 
            Instances[] files, int numSamples, int labOrUnlab) throws Exception {
        
        int added = 0, indexControl = 0, toRemove, loopsWithoutRemove = 0;
        
        do {
            
            double rootValue = classificador.classifyInstance(files[indexControl].
                    firstInstance());
            toRemove = 0;
            
            for (int i = 1; i < files[indexControl].numInstances(); i++) {
                double instValue = classificador.classifyInstance(files[indexControl].
                        instance(i));
                if((rootValue != instValue) || (i == files[indexControl].
                        numInstances() - 1)){
                    ret.add(files[indexControl].instance(i));
                    toRemove = i;
                    added++;
                    break;
                }       
            }              
            if(toRemove != 0){
                Instances aux = new Instances(files[indexControl]);
                aux.delete();
                for (int i = 0; i < files[indexControl].numInstances(); i++) {
                    if(i == toRemove){/*do nothing*/}
                    else
                        aux.add(files[indexControl].instance(i));
                }
                files[indexControl] = aux;
                loopsWithoutRemove = 0;
            } else
                loopsWithoutRemove++;
            
            if(added == numSamples)
                break;
            
            if (loopsWithoutRemove == files.length + 1){
                outOfSamples = true;
                break;
            }
            
            if(indexControl == files.length - 1)
                indexControl = 0;
            else
                indexControl++;
            
        } while (true);
        
        if(labOrUnlab == 0)
            z2iMultiple = files;
        else if(labOrUnlab == 1)
            z2iiMultiple = files;
        
        return ret;   
    }

    private static Instances selectSamples(Instances ret, Instances file, 
            int numSamples) {

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
    
    private static void loadsFilesMultipleLists(String uuid, File[] files,
            String basesSavePath) throws Exception {
        
        List<Instances> z2iList = new ArrayList<>();
        List<Instances> z2iiList = new ArrayList<>();
        List<Integer> indexes = new ArrayList<>();
        
        boolean openedZ3 = false;
        
        for (int i = 0; i < files.length; i++) {
            if(files[i].getName().startsWith(uuid) && files[i].getName().
                       contains("RDS") && files[i].getName().contains("_z2i_")
                    && files[i].getName().contains("lista")){
                
                indexes.add(Integer.valueOf(files[i].getName().split("_lista_")[1].
                        split(".arff")[0]));
                
                Instances aux = IO.open(files[i].getAbsolutePath());
                aux.setClassIndex(aux.numAttributes() - 1);
                z2iList.add(aux);
                
                aux = IO.open(files[i].getAbsolutePath().replace("_z2i_", 
                        "_z2ii_"));
                aux.setClassIndex(aux.numAttributes() - 1);
                z2iiList.add(aux);
         
                if(!openedZ3){
                    z3 = IO.open(basesSavePath + files[i].getName().
                            split("_z2")[0] + "_z3.arff");
                    z3.setClassIndex(z3.numAttributes() - 1);
                    openedZ3 = true;
                }       
            }    
        }
        
        z2iMultiple = new Instances[indexes.size()];
        z2iiMultiple = new Instances[indexes.size()];
        
        for (int i = 0; i < indexes.size(); i++) {
            z2iMultiple[indexes.get(i)] = z2iList.get(i);
            z2iiMultiple[indexes.get(i)] = z2iiList.get(i);
        }
    }

    private static void loadsFiles(File file, String basesSavePath) 
            throws Exception {
        
        z2iSingle = IO.open(file.getAbsolutePath());
        z2iSingle.setClassIndex(z2iSingle.numAttributes() - 1);
        z2iiSingle = IO.open(file.getAbsolutePath().replace("_z2i_", "_z2ii_"));
        z2iiSingle.setClassIndex(z2iiSingle.numAttributes() - 1);
        z3 = IO.open(basesSavePath + file.getName().split("_z2")[0] + "_z3.arff");
        z3.setClassIndex(z3.numAttributes() - 1);
    }

    private static void makesClassification(Instances z2i) throws Exception {
        
        for (String classifier : classifiers) {
            switch (classifier) {
                case "SVM":
                    SVM svm = new SVM(z2i, z3);
                    classificador = svm.getClassifier();
                    break;
                case "RF":
                    RF rf = new RF(z2i, z3);
                    classificador = rf.getClassifier();
                    break;
            }
        }
    }

    private static void routineSingleList(File file) throws Exception {
        
        loadsFiles(file, basesSavePath);
                    
        Instances z2i = new Instances(z2iSingle);
        z2i.delete();
        Instances z2ii = new Instances(z2iiSingle);
        z2ii.delete();

        do {
            z2i = selectSamples(z2i, z2iSingle, z2iSingle.numClasses() * xNumClasses);
            //z2ii = selectSamples(z2ii, _z2ii, _z2i.numClasses());
            
            makesClassification(z2i);

        } while (!outOfSamples);
    }

    private static void routineMultipleLists(String uuid, File[] files) throws Exception {
        
        loadsFilesMultipleLists(uuid, files, basesSavePath);

        Instances z2i = new Instances(z2iMultiple[0]);
        z2i.delete();
        Instances z2ii = new Instances(z2iiMultiple[0]);
        z2ii.delete();

        for (Instances z2iMultiple1 : z2iMultiple)
            z2i.add(z2iMultiple1.instance(0)); //raizes

        makesClassification(z2i);
        
        do {
            z2i = selectSamplesMultipleLists(z2i, z2iMultiple, 
                    z2iMultiple[0].numClasses() * xNumClasses, 0);
            //z2ii = selectSamples(z2ii, _z2ii, _z2i.numClasses());

            makesClassification(z2i);

        } while (outOfSamples == false);
    }
    
}
