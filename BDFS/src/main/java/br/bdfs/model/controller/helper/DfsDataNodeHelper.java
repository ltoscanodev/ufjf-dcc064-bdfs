/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.bdfs.model.controller.helper;

import br.bdfs.context.AppContext;
import br.bdfs.exceptions.AuthException;
import br.bdfs.exceptions.DfsException;
import br.bdfs.exceptions.ExistsException;
import br.bdfs.exceptions.NotFoundException;
import br.bdfs.model.DfsDataNode;
import br.bdfs.model.DfsUser;
import br.bdfs.model.controller.DfsDataNodeJpaController;
import java.util.Date;
import java.util.List;
import javax.persistence.NoResultException;

/**
 *
 * @author igor6
 */
public class DfsDataNodeHelper {
    public static void connectDataNode(String dataNodeIp, int dataNodePort) throws NotFoundException, DfsException
    {
        if(!existsDataNode(dataNodeIp, dataNodePort))
        {
            createDataNode(dataNodeIp, dataNodePort);
            return;
        }
        
        DfsDataNodeJpaController dfsDataNodeJpaController = AppContext.createInstance(DfsDataNodeJpaController.class);
        DfsDataNode dfsDataNode = dfsDataNodeJpaController.findDataNode(dataNodeIp, dataNodePort);
        
        if(dfsDataNode == null)
        {
            throw new NotFoundException("O nó de dados não foi encontrado.");
        }
        
        dfsDataNode.setConnected((short)0);
        
        dfsDataNodeJpaController.edit(dfsDataNode);
    }
    
    public static void createDataNode(String token, String dataNodeIp, int dataNodePort) throws DfsException
    {
        if(existsDataNode(dataNodeIp, dataNodePort))
        {
            throw new ExistsException("O nó de dados já existe.");
        }
        
        try {
            DfsUser user = DfsUserHelper.findUserByToken(token);
            
            DfsDataNodeJpaController dfsDataNodeJpaController = AppContext.createInstance(DfsDataNodeJpaController.class);
             
            DfsDataNode dataNode = new DfsDataNode();
            dataNode.setAddressIp(dataNodeIp);
            dataNode.setAddressPort(dataNodePort);
            dataNode.setLastAccess(new Date());
            
            dfsDataNodeJpaController.create(dataNode);
            
            DfsDataNodeUserHelper.createDataNodeUser(dataNode, user);
            
        } catch (AuthException ex) {
            throw new NotFoundException(("Usuário não logado."));
        }
    }
    
    public static void createDataNode(String dataNodeIp, int dataNodePort) throws DfsException
    {
        if(existsDataNode(dataNodeIp, dataNodePort))
        {
            throw new ExistsException("O nó de dados já existe.");
        }
        
            
        DfsDataNodeJpaController dfsDataNodeJpaController = AppContext.createInstance(DfsDataNodeJpaController.class);

        DfsDataNode dataNode = new DfsDataNode();
        dataNode.setAddressIp(dataNodeIp);
        dataNode.setAddressPort(dataNodePort);
        dataNode.setConnected((short) 1);
        dataNode.setLastAccess(new Date());

        dfsDataNodeJpaController.create(dataNode);
    }
    
    public static void deleteDataNode(String dataNodeIp, int dataNodePort) throws NotFoundException
    {
        DfsDataNodeJpaController dfsDataNodeJpaController = AppContext.createInstance(DfsDataNodeJpaController.class);
        DfsDataNode dfsDataNode = dfsDataNodeJpaController.findDataNode(dataNodeIp, dataNodePort);
        
        if(dfsDataNode == null)
        {
            throw new NotFoundException("O nó de dados não foi encontrado.");
        }
        
        dfsDataNodeJpaController.remove(dfsDataNode.getId());
    }
    
    public static void disconnectDataNode(String dataNodeIp, int dataNodePort) throws NotFoundException
    {
        DfsDataNodeJpaController dfsDataNodeJpaController = AppContext.createInstance(DfsDataNodeJpaController.class);
        DfsDataNode dfsDataNode = dfsDataNodeJpaController.findDataNode(dataNodeIp, dataNodePort);
        
        if(dfsDataNode == null)
        {
            throw new NotFoundException("O nó de dados não foi encontrado.");
        }
        
        dfsDataNode.setConnected((short)0);
        
        dfsDataNodeJpaController.edit(dfsDataNode);
    }
    
    public static List<DfsDataNode> findDataNode(String token) throws NotFoundException
    {
        
        try {
            DfsUser user = DfsUserHelper.findUserByToken(token);
            
            DfsDataNodeJpaController dfsDataNodeJpaController = AppContext.createInstance(DfsDataNodeJpaController.class);
            return dfsDataNodeJpaController.findUserDataNodes(token);
            
        } catch (AuthException ex) {
            throw new NotFoundException(("Usuário não logado."));
        }
    }
    
    public static List<DfsDataNode> findAvailableDataNodes() throws NotFoundException
    {
            DfsDataNodeJpaController dfsDataNodeJpaController = AppContext.createInstance(DfsDataNodeJpaController.class);
            return dfsDataNodeJpaController.findAllEntities();
    }
    
    private static DfsDataNode findDataNode(String dataNodeIp, int dataNodePort) throws DfsException
    {        
        try
        {
            DfsDataNodeJpaController dfsDataNodeJpaController = AppContext.createInstance(DfsDataNodeJpaController.class);
            return dfsDataNodeJpaController.findDataNode(dataNodeIp, dataNodePort);
        }
        catch (NoResultException ex) 
        {
            throw new NotFoundException("Nó de dados não encontrado");
        }
    }
    
    public static boolean existsDataNode(String dataNodeIp, int dataNodePort)
    {
        try 
        {
            return (findDataNode(dataNodeIp, dataNodePort) != null);
        } 
        catch (DfsException ex) 
        {
            return false;
        }
    }
}
