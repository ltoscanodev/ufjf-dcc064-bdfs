/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.bdfs.model.controller.helper;

import br.bdfs.context.AppContext;
import br.bdfs.exceptions.DfsException;
import br.bdfs.exceptions.InvalidPathException;
import br.bdfs.exceptions.NotFoundException;
import br.bdfs.model.DfsDirectory;
import br.bdfs.model.DfsFile;
import br.bdfs.model.controller.DfsFileJpaController;
import java.util.Date;
import java.util.List;

/**
 *
 * @author igor6
 */
public class DfsFileHelper 
{
    public static DfsFile createUserFile(String token, String name, String filePath, String dirPath, String extension, int size) throws DfsException
    {
        DfsDirectory dir = DfsDirectoryHelper.findUserDirectory(token, dirPath);
        
        if(dir == null)
        {
            throw new NotFoundException("Diretório não encontrado");
        }
        
        DfsFileJpaController dfsFileJpaController = AppContext.createInstance(DfsFileJpaController.class);
        
        DfsFile dfsFile = new DfsFile();
        
        dfsFile.setName(name);
        dfsFile.setExtension(extension);
        dfsFile.setPath(filePath);
        dfsFile.setAccessTime(new Date());
        dfsFile.setCreationTime(new Date());
        dfsFile.setModificationTime(new Date());
        dfsFile.setDirectory(dir);
        dfsFile.setSize(size);
        
        dfsFileJpaController.create(dfsFile);
        
        return dfsFile;
    }
    
    public static boolean existsFile(DfsFile dfsFile)
    {
        DfsFileJpaController dfsFileJpaController = AppContext.createInstance(DfsFileJpaController.class);
        return dfsFileJpaController.findEntity(dfsFile.getId()) !=  null;
    }
    
    public static boolean existsFile(String token, String path) throws InvalidPathException
    {
        DfsFileJpaController dfsFileJpaController = AppContext.createInstance(DfsFileJpaController.class);
        try {
            return dfsFileJpaController.getFile(token, path) != null;
        } catch (Exception ex) {
            return false;
        }
    }
    
    public static void updateFile(DfsFile dfsFile)
    {
        DfsFileJpaController dfsFileJpaController = AppContext.createInstance(DfsFileJpaController.class);
        dfsFileJpaController.edit(dfsFile);
    }
    
    public static List<DfsFile> getUserFiles(String token, String path)
    {
        DfsFileJpaController dfsFileJpaController = AppContext.createInstance(DfsFileJpaController.class);
        return dfsFileJpaController.getChildFiles(token, path);
    }
    
    public static DfsFile getUserFile(String token, String path) throws InvalidPathException
    {
        DfsFileJpaController dfsFileJpaController = AppContext.createInstance(DfsFileJpaController.class);
        return dfsFileJpaController.getFile(token, path);
    }
    
    public static boolean deleteUserFile(String token, String filePath) throws NotFoundException, InvalidPathException
    {
        DfsFileJpaController dfsFileJpaController = AppContext.createInstance(DfsFileJpaController.class);
        return dfsFileJpaController.deleteUserFile(token, filePath);
    }
}
