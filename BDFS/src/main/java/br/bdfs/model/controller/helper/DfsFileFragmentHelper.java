/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.bdfs.model.controller.helper;

import br.bdfs.context.AppContext;
import br.bdfs.exceptions.DfsException;
import br.bdfs.exceptions.ExistsException;
import br.bdfs.exceptions.InvalidPathException;
import br.bdfs.exceptions.NotFoundException;
import br.bdfs.model.DfsDataNode;
import br.bdfs.model.DfsFile;
import br.bdfs.model.DfsFileFragment;
import br.bdfs.model.controller.DfsFileFragmentJpaController;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.persistence.NoResultException;

/**
 *
 * @author igor6
 */
public class DfsFileFragmentHelper {
    
    public static void createFileFragment(String guid, int offset, int size, DfsDataNode dataNode, DfsFile dfsFile) throws ExistsException
    {
        if(existsFileFragment(guid))
        {
            throw new ExistsException("O fragmento já existe.");
        }
            
        DfsFileFragmentJpaController dfsFileFragmentJpaController = AppContext.createInstance(DfsFileFragmentJpaController.class);
        List<DfsDataNode> auxDataNodeList = new ArrayList<>();
        auxDataNodeList.add(dataNode);

        DfsFileFragment dfsFileFragment = new DfsFileFragment();
        dfsFileFragment.setFile(dfsFile);
        dfsFileFragment.setGuid(guid);
        dfsFileFragment.setOffset(offset);
        dfsFileFragment.setSize(size);
        dfsFileFragment.setDfsDataNodeList(auxDataNodeList);
        dfsFileFragment.setLastAccess(new Date());
        
        dfsFileFragmentJpaController.create(dfsFileFragment);
    }
    
    public static boolean deleteFileUser(String token, String filePath) throws InvalidPathException
    {
        DfsFileFragmentJpaController dfsFileFragmentJpaController = AppContext.createInstance(DfsFileFragmentJpaController.class);
        return dfsFileFragmentJpaController.deleteUserFile(token, filePath);
    }
    
    public static boolean existsFileFragment(String guid)
    {
        try 
        {
            return (findFileFragment(guid) != null);
        } 
        catch (DfsException ex) 
        {
            return false;
        }
    }
    
    public static DfsFileFragment findFileFragment(String guid) throws NotFoundException
    {
        try
        {
            DfsFileFragmentJpaController dfsFileFragmentJpaController = AppContext.createInstance(DfsFileFragmentJpaController.class);
            return dfsFileFragmentJpaController.findByNamedQuerySingle("DfsFileFragment.findByGuid", "guid", guid);
        }
        catch (NoResultException ex) 
        {
            throw new NotFoundException("Fragmento não encontrado");
        }
    }
    
    public static List<DfsFileFragment> getFileFragments(String token, String filePath) throws InvalidPathException
    {
        DfsFileFragmentJpaController dfsFileFragmentJpaController = AppContext.createInstance(DfsFileFragmentJpaController.class);
        return dfsFileFragmentJpaController.getFileFragments(token, filePath);
    }
}
