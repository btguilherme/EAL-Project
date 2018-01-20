/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eal.main;

import eal.selection.MultipleLists;
import eal.selection.UniqueList;
import eal.supervised.OPF;
import eal.supervised.RF;
import eal.supervised.SVM;
import eal.unsupervised.CollectiveWrapperOPF;
import eal.unsupervised.CollectiveWrapperRF;
import eal.unsupervised.CollectiveWrapperSVM;
import eal.unsupervised.YATSIOPF;
import eal.unsupervised.YATSIRF;
import eal.unsupervised.YATSISVM;
import eal.utils.IO;
import eal.utils.ReadProperties;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import weka.classifiers.Classifier;
import weka.core.Instances;

/**
 *
 * @author guilherme
 */
public class EALSelectionClassification {

    private static String[] classifiers;
    private static int xNumClasses;
    private static Instances z2iSingle;
    private static Instances z2iiSingle;
    private static Instances[] z2iMultiple;
    private static Instances[] z2iiMultiple;
    private static Instances z3;
    private static String basesSavePath;
    private static Classifier classificador;
    private static String savePath;
    private static String executionUUID;
    private static double acc;
    

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException, Exception {
        
        executionUUID = UUID.randomUUID().toString();
        
        ReadProperties rp = new ReadProperties();
        classifiers = rp.getClassifiers();
        xNumClasses = rp.getxNumClasses();
        basesSavePath = rp.getBasesSavePath();
        String splitsSortedPath = rp.getSplitsSorted();
        String methodsSort[] = rp.getSort();
        
        savePath = System.getProperty("user.dir").concat(File.separator).
                concat("results").concat(File.separator);
        
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
                        
                                routineMultipleListsClu(uuid, files, methodSort);
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
        
        String save = System.getProperty("user.dir").concat(File.separator).
                    concat("results").concat(File.separator).concat("txt").
                    concat(File.separator).concat(file.getName().split("_z2")[0].
                    concat("_acc.txt")).concat("_").concat(".txt");
        
        IO.saveConcat(file.getName().replace("_z2i_", "_"), save);

        int contIt = 0;
        do {
            z2i = ul.selectSamplesUniqueList(z2i, z2iSingle,
                    z2iSingle.numClasses() * xNumClasses);
            
            z2ii = ul.selectSamplesUniqueList(z2ii, z2iiSingle,
                    z2iiSingle.numClasses());
            
            IO.save(z2i, savePath + file.getName().split(".arff")[0].concat("_it_").
                    concat(String.valueOf(contIt)).concat(".arff"));
            IO.save(z2ii, savePath + file.getName().replace("z2i", "z2ii").split(".arff")[0].
                    concat("_it_").concat(String.valueOf(contIt)).concat(".arff"));
            
            makesClassification(z2i, z2ii);
            
            IO.saveConcat(String.valueOf(acc), save);
            
            arrumar a parte de salvar as acuracias
            
            contIt++;
        
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

        int contIt = 0;
        
        for (Instances z2iMultiple1 : z2iMultiple)
            z2i.add(z2iMultiple1.firstInstance()); //raizes
        
        for (int i = 0; i < z2iiMultiple.length / 2; i++)
            z2ii.add(z2iiMultiple[i].firstInstance());
        
        IO.save(z2i, savePath + ml.getFileName().split("lista_")[0].concat("it_").
                concat(String.valueOf(contIt)).concat(".arff"));
        
        IO.save(z2ii, savePath + ml.getFileName().replace("z2i", "z2ii").
                split("lista_")[0].concat("it_").concat(String.valueOf(contIt)).
                concat(".arff"));
        
        contIt++;
        
        makesClassification(z2i, z2ii);
        
        do {
            z2i = ml.selectSamplesMultipleLists(classificador, z2i, 
                    z2iMultiple, z2iMultiple[0].numClasses() * xNumClasses, 0);
            
            z2ii = ml.selectSamplesMultipleLists(classificador, z2ii, 
                    z2iiMultiple, z2iiMultiple[0].numClasses(), 1);
            
            IO.save(z2i, savePath + ml.getFileName().split("lista_")[0].concat("it_").
                    concat(String.valueOf(contIt)).concat(".arff"));

            IO.save(z2ii, savePath + ml.getFileName().replace("z2i", "z2ii").
                    split("lista_")[0].concat("it_").concat(String.valueOf(contIt)).
                    concat(".arff"));
            
            makesClassification(z2i, z2ii);
            
            contIt++;
            
        } while (!ml.isOutOfSamples());
    }
    
    private static void routineMultipleListsClu(String uuid, File[] files, 
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
        
        int contIt = 0;
        
        do {
            z2i = ml.selectSamplesMultipleListsClu(z2i, z2iMultiple, 
                    z2iMultiple[0].numClasses() * xNumClasses, 0);
            z2ii = ml.selectSamplesMultipleListsClu(z2ii, z2iiMultiple, 
                    z2iiMultiple[0].numClasses(), 1);
            
            IO.save(z2i, savePath + ml.getFileName().split("lista_")[0].concat("it_").
                    concat(String.valueOf(contIt)).concat(".arff"));

            IO.save(z2ii, savePath + ml.getFileName().replace("z2i", "z2ii").
                    split("lista_")[0].concat("it_").concat(String.valueOf(contIt)).
                    concat(".arff"));
            
            makesClassification(z2i, z2ii);
            
            contIt++;
        } while (!ml.isOutOfSamples());
    }
    
    private static void makesClassification(Instances z2i, Instances z2ii) throws Exception {
        
        for (String classifier : classifiers) {
            switch (classifier) {
                //supervised
                case "SVM":
                    SVM svm = new SVM(z2i, z3);
                    classificador = svm.getClassifier();
                    acc = svm.getAcc();
                    break;
                case "RF":
                    RF rf = new RF(z2i, z3);
                    classificador = rf.getClassifier();
                    acc = rf.getAcc();
                    break;
                case "OPF":
                    OPF opf = new OPF(z2i, z3);
                    classificador = opf.getClassifier();
                    acc = opf.getAcc();
                    break;
                //semisupervised
                case "YSVM":
                    YATSISVM ysvm = new YATSISVM(z2i, z2ii, z3);
                    classificador = ysvm.getClassifier();
                    acc = ysvm.getAcc();
                    break;
                case "YRF":
                    YATSIRF yrf = new YATSIRF(z2i, z2ii, z3);
                    classificador = yrf.getClassifier();
                    acc = yrf.getAcc();
                    break;
                case "YOPF":
                    YATSIOPF yopf = new YATSIOPF(z2i, z2ii, z3);
                    classificador = yopf.getClassifier();
                    acc = yopf.getAcc();
                    break;
                case "WSVM":
                    CollectiveWrapperSVM wsvm = new CollectiveWrapperSVM(z2i, z2ii, z3);
                    classificador = wsvm.getClassifier();
                    acc = wsvm.getAcc();
                    break;
                case "WRF":
                    CollectiveWrapperRF wrf = new CollectiveWrapperRF(z2i, z2ii, z3);
                    classificador = wrf.getClassifier();
                    acc = wrf.getAcc();
                    break;
                case "WOPF":
                    CollectiveWrapperOPF wopf = new CollectiveWrapperOPF(z2i, z2ii, z3);
                    classificador = wopf.getClassifier();
                    acc = wopf.getAcc();
                    break;
            }
        }
    }

}