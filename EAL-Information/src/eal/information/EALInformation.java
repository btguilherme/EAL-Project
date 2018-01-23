/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eal.information;

import eal.utils.IO;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author guilherme
 */
public class EALInformation {

    /**
     * @param args the command line arguments
     */
    
    private static final String SEP = File.separator;
    
    public static void main(String[] args) {
        
        String txtFilesPath = System.getProperty("user.dir").split("EAL-Informat"
                + "ion")[0].concat("txt-files").concat(SEP);
        
        File[] txtFiles = IO.getFiles("txt", txtFilesPath);
        
        
        
        
        
        Set<String> uuids = new HashSet<>();
        for (File txtFile : txtFiles)
            uuids.add(txtFile.getName().split("@")[0]);
        
        for (String uuid : uuids) {
            System.out.println(uuid);
        }
        
        /*
        pegar uuid
        numero do split
        metodo de ordenação
        se é acc, test ou train
        qual classificador
         */
        
        
        
    }
    
}
