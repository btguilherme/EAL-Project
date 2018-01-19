/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eal.classification;

import eal.selection.MultipleLists;
import eal.selection.UniqueList;
import eal.supervised.RF;
import eal.supervised.SVM;
import eal.utils.IO;
import eal.utils.ReadProperties;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
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
        String methodsSort[] = rp.getSort();
        
        File[] files = IO.getFiles(".arff", splitsSortedPath);
        Set<String> uuids = new HashSet<>();
        
        for (File file : files)
            uuids.add(file.getName().split("@")[0]);
        
        for (String uuid : uuids) {
            for (String methodSort : methodsSort) {
                boolean did = false;
                for (int i = 0; i < files.length; i++) {
                    switch(methodSort){
                        case "AFC":
                        case "Rand":
                            if(files[i].getName().startsWith(uuid) && files[i].
                                    getName().contains(methodSort) && files[i].
                                            getName().contains("_z2i_"))
                                routineSingleList(files[i]);
                            break;
                        case "Clu":
                            if(files[i].getName().startsWith(uuid) && files[i].
                                    getName().contains(methodSort) && files[i].
                                            getName().contains("_z2i_") && !did){
                        
                                routineMultipleLists2(uuid, files, methodSort);
                                did = true;
                            }
                            break;
                        case "RDBS":
                        case "RDS":
                            if(files[i].getName().startsWith(uuid) && files[i].
                                    getName().contains(methodSort) && files[i].
                                            getName().contains("_z2i_") && !did){
                                
                                routineMultipleLists(uuid, files, methodSort);
                                did = true;
                            }
                            break;
                    }
                }
                System.out.println("");
            }
        }
    }
    
    private static void routineSingleList(File file) throws Exception {
        
        UniqueList ul = new UniqueList();
        
        ul.loadsFilesUniqueList(file, basesSavePath);
        
        z2iSingle = ul.getZ2iSingle();
        z2iiSingle = ul.getZ2iiSingle();
        z3 = ul.getZ3();
        
        Instances z2i = new Instances(z2iSingle);
        z2i.delete();
        Instances z2ii = new Instances(z2iiSingle);
        z2ii.delete();

        do {
            z2i = ul.selectSamplesUniqueList(z2i, z2iSingle,
                    z2iSingle.numClasses() * xNumClasses);
            
            makesClassification(z2i);
        
        } while (!ul.isOutOfSamples());
    }

    private static void routineMultipleLists(String uuid, File[] files, 
            String method) throws Exception {
        
        MultipleLists ml = new MultipleLists();
        
        ml.loadsFilesMultipleLists(uuid, files, basesSavePath, method);

        z2iMultiple = ml.getZ2iMultiple();
        z2iiMultiple = ml.getZ2iiMultiple();
        z3 = ml.getZ3();
        
        Instances z2i = new Instances(z2iMultiple[0]);
        z2i.delete();
        Instances z2ii = new Instances(z2iiMultiple[0]);
        z2ii.delete();

        for (Instances z2iMultiple1 : z2iMultiple)
            z2i.add(z2iMultiple1.instance(0)); //raizes

        makesClassification(z2i);
        
        
        fazer os classificadores semi-supervisonados
        
        
        
        
        do {
            z2i = ml.selectSamplesMultipleLists(classificador, z2i, 
                    z2iMultiple, z2iMultiple[0].numClasses() * xNumClasses, 0);
            makesClassification(z2i);
        } while (!ml.isOutOfSamples());
    }
    
    private static void routineMultipleLists2(String uuid, File[] files, 
            String method) throws Exception {
        
        MultipleLists ml = new MultipleLists();
        
        ml.loadsFilesMultipleLists(uuid, files, basesSavePath, method);

        z2iMultiple = ml.getZ2iMultiple();
        z2iiMultiple = ml.getZ2iiMultiple();
        z3 = ml.getZ3();
        
        Instances z2i = new Instances(z2iMultiple[0]);
        z2i.delete();
        Instances z2ii = new Instances(z2iiMultiple[0]);
        z2ii.delete();
        
        do {
            z2i = ml.selectSamplesMultipleLists2(z2i, z2iMultiple, 
                    z2iMultiple[0].numClasses() * xNumClasses, 0);
            makesClassification(z2i);
        } while (!ml.isOutOfSamples());
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

}
