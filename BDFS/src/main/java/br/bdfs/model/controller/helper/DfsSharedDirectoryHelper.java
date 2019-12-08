package br.bdfs.model.controller.helper;

import br.bdfs.context.AppContext;
import br.bdfs.exceptions.DfsException;
import br.bdfs.exceptions.ExistsException;
import br.bdfs.model.DfsDirectory;
import br.bdfs.model.DfsSharedDirectory;
import br.bdfs.model.DfsUser;
import br.bdfs.model.controller.DfsDirectoryJpaController;
import br.bdfs.model.controller.DfsSharedDirectoryJpaController;
import br.bdfs.node.filesystem.DfsPath;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.PersistenceException;

/**
 *
 * @author ltosc
 */
public class DfsSharedDirectoryHelper 
{
    public static void createSharedDirectory(String token, DfsPath dirPath) throws DfsException
    {
        if(dirPath.size() != 1)
        {
            throw new ExistsException("Apenas o diretório raiz deve ser usado para criar um diretório compartilhado");
        }
        
        if(DfsDirectoryHelper.existsUserDirectory(token, dirPath.toString()))
        {
            throw new ExistsException("O diretório compartilhado já existe");
        }
        
        DfsUser user = DfsUserHelper.findUserByToken(token);
        
        DfsDirectoryJpaController dfsDirectoryJpaController = AppContext.createInstance(DfsDirectoryJpaController.class);
        DfsSharedDirectoryJpaController dfsSharedDirectoryJpaController = AppContext.createInstance(DfsSharedDirectoryJpaController.class);
        
        
        DfsDirectory dir = new DfsDirectory();
        dir.setName(dirPath.name());
        dir.setPath(dirPath.current());
        dir.setCreationTime(new Date());
        dir.setParentDirectory(null);
        dfsDirectoryJpaController.create(dir);
        
        DfsSharedDirectory sharedDir = new DfsSharedDirectory();
        sharedDir.setSharedDirectory(dir);
        dfsSharedDirectoryJpaController.create(sharedDir);
        
        sharedDir.setDfsUserList(new ArrayList<>());
        sharedDir.getDfsUserList().add(user);
        dfsSharedDirectoryJpaController.edit(sharedDir);
    }
    
    public static void addUserInSharedDirectory(String token, String username, String sharedDirPath) throws DfsException
    {
        DfsSharedDirectoryJpaController dfsSharedDirectoryJpaController = AppContext.createInstance(DfsSharedDirectoryJpaController.class);
        
        List<DfsSharedDirectory> sharedDirList = getSharedDirectories(token);
        DfsUser user = DfsUserHelper.findByUserName(username);
        
        for(DfsSharedDirectory sharedDir : sharedDirList)
        {
            if(sharedDir.getSharedDirectory().getPath().equalsIgnoreCase(sharedDirPath))
            {
                try
                {
                    sharedDir.getDfsUserList().add(user);
                    dfsSharedDirectoryJpaController.edit(sharedDir);
                }
                catch(IllegalArgumentException | PersistenceException ex)
                {
                    throw new DfsException("Não foi possível adicionar o usuário ao diretório compartilhado");
                }
                
                return;
            }
        }
        
        throw new DfsException("Diretório compartilhado não encontrado");
    }
    
    public static List<DfsSharedDirectory> getSharedDirectories(String token) throws DfsException
    {
        DfsUser user = DfsUserHelper.findUserByToken(token);
        return user.getDfsSharedDirectoryList();
    }
}
