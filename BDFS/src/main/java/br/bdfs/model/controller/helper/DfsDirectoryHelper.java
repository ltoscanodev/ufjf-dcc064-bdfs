package br.bdfs.model.controller.helper;

import br.bdfs.context.AppContext;
import br.bdfs.exceptions.DfsException;
import br.bdfs.exceptions.ExistsException;
import br.bdfs.exceptions.NotFoundException;
import br.bdfs.helper.PathHelper;
import br.bdfs.node.filesystem.DfsPath;
import br.bdfs.model.DfsDirectory;
import br.bdfs.model.controller.DfsDirectoryJpaController;
import java.util.Date;
import java.util.List;
import javax.persistence.NoResultException;

/**
 *
 * @author ltosc
 */
public class DfsDirectoryHelper 
{
    public static DfsDirectory findUserDirectory(String token, String path) throws DfsException
    {
        try
        {
            DfsDirectoryJpaController dfsDirectoryJpaController = AppContext.createInstance(DfsDirectoryJpaController.class);
            return dfsDirectoryJpaController.findUserDirectory(token, path);
        }
        catch (NoResultException ex) 
        {
            throw new NotFoundException("Diretório não encontrado");
        }
    }
    
    public static boolean existsUserDirectory(String token, String dirPath)
    {
        try 
        {
            return (findUserDirectory(token, dirPath) != null);
        } 
        catch (DfsException ex) 
        {
            return false;
        }
    }
    
    public static void createUserDirectory(String token, DfsPath dirPath, boolean recursively) throws DfsException
    {
        if(existsUserDirectory(token, dirPath.toString()))
        {
            throw new ExistsException("O diretório já existe");
        }
        
        DfsDirectoryJpaController dfsDirectoryJpaController = AppContext.createInstance(DfsDirectoryJpaController.class);
        
        DfsDirectory parentDir = null;
        DfsDirectory dir;
        
        while (dirPath.hasNext()) 
        {
            String path = dirPath.next();
            String name = dirPath.name();
            
            try 
            {
                parentDir = findUserDirectory(token, path);
            } 
            catch (NotFoundException ex) 
            {
                if(dirPath.isLast() || recursively)
                {
                    dir = new DfsDirectory();
                    dir.setName(name);
                    dir.setPath(path);
                    dir.setCreationTime(new Date());
                    dir.setParentDirectory(parentDir);
                    dfsDirectoryJpaController.create(dir);
                    
                    parentDir = dir;
                }
                else
                {
                    throw new NotFoundException(String.format("O subdiretório %s do caminho não existe", name));
                }
            }
        }
    }
    
    public static List<DfsDirectory> getUserDirectories(String token, String parentPath) throws DfsException
    {
        DfsDirectoryJpaController dfsDirectoryJpaController = AppContext.createInstance(DfsDirectoryJpaController.class);
        return dfsDirectoryJpaController.getChildDirectories(token, parentPath);
    }
    
    public static boolean deleteUserDirectory(String token, String path) throws DfsException
    {
        DfsDirectoryJpaController dfsDirectoryJpaController = AppContext.createInstance(DfsDirectoryJpaController.class);
        return dfsDirectoryJpaController.deleteUserDirectory(token, path);
    }
}
