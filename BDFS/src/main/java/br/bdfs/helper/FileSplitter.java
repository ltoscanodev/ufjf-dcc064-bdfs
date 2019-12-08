package br.bdfs.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ltosc
 */
public class FileSplitter
{
    public static void split(String inputPath, List<String> outputPathList, int splitCount) throws FileNotFoundException
    {
        File inputFile = new File(inputPath);
        
        if (!inputFile.exists()) 
        {
            throw new FileNotFoundException(String.format("O arquivo %s não foi encontrado", inputPath));
        }
        
        String inputDirectoryPath = inputFile.getParent();
        long inputFileSize = inputFile.length();
        
        double fragmentSize = ((double)inputFileSize / (double)splitCount);
        long splitFileSize = (long)Math.ceil(fragmentSize);
        
        int index = 0;
        byte[] buffer = new byte[Math.toIntExact(splitFileSize)];
        long totalRead = 0;
        long read;
        
        try (FileInputStream fileInputStream = new FileInputStream(inputFile)) 
        {
            do 
            {
                read = Math.min(splitFileSize, (inputFileSize - totalRead));
                fileInputStream.read(buffer, 0, Math.toIntExact(read));
                totalRead += read;

                try (FileOutputStream fileOutputStream = new FileOutputStream(new File(String.format("%s%s%s", inputDirectoryPath, File.separator, outputPathList.get(index++))))) 
                {
                    fileOutputStream.write(buffer, 0, Math.toIntExact(read));
                }
            } 
            while (index < splitCount);
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(FileSplitter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void merge(String outputPath, List<String> inputPathList, int splitCount) throws FileNotFoundException
    {
        File outputFile = new File(outputPath);
        
        if (outputFile.exists()) 
        {
            throw new FileNotFoundException(String.format("O arquivo %s não foi encontrado", outputPath));
        }
        
        String outputDirectoryPath = outputFile.getParent();
        
        int index = 0;
        
        try (FileOutputStream fileOutputStream = new FileOutputStream(outputFile)) 
        {
            do 
            {
                File splitFile = new File(inputPathList.get(index++));
                
                if (!splitFile.exists()) 
                {
                    throw new FileNotFoundException(String.format("A parte %s não foi encontrada", splitFile.getAbsolutePath()));
                }
                
                byte[] buffer = new byte[Math.toIntExact(splitFile.length())];
                
                try (FileInputStream fileInputStream = new FileInputStream(splitFile))
                {
                    fileInputStream.read(buffer);
                }
                
                fileOutputStream.write(buffer, 0, buffer.length);
            } 
            while (index < splitCount);
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(FileSplitter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
