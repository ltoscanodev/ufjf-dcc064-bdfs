/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.bdfs.model.controller;

import br.bdfs.model.DfsDataNode;
import java.util.List;
import javax.persistence.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author igor6
 */
@Repository
public class DfsDataNodeJpaController extends AbstractJpaController<DfsDataNode> 
{

    public DfsDataNodeJpaController() {
        super(DfsDataNode.class);
    }

    public DfsDataNode findDataNode(String ip, int port)
    {
        String sql = "SELECT dfs_data_node.id, dfs_data_node.address_ip, dfs_data_node.address_port\n"
                + "FROM \n"
                + "dfs_data_node\n"
                + "WHERE \n"
                + "	dfs_data_node.address_ip = ? AND dfs_data_node.address_port = ?";
        
        Query sqlQuery = getEntityManager().createNativeQuery(sql, DfsDataNode.class);
        sqlQuery.setParameter(1, ip);
        sqlQuery.setParameter(2, port);
        
        return (DfsDataNode)sqlQuery.getSingleResult();
    }

    public DfsDataNode findDataNode(int id)
    {
        String sql = "SELECT dfs_data_node.id, dfs_data_node.address_ip, dfs_data_node.address_port\n"
                + "FROM \n"
                + "dfs_data_node\n"
                + "WHERE \n"
                + "	dfs_data_node.id = ?";
        
        Query sqlQuery = getEntityManager().createNativeQuery(sql, DfsDataNode.class);
        sqlQuery.setParameter(1, id);
        
        return (DfsDataNode)sqlQuery.getSingleResult();
    }
    
    public List<DfsDataNode> findUserDataNodes(String token)
    {
        String sql = "SELECT dfs_data_node.id, dfs_data_node.address_ip, dfs_data_node.address_port\n"
                + "FROM \n"
                + "dfs_data_node INNER JOIN\n"
                + "(SELECT dfs_data_node_user.id, dfs_data_node_user.dfs_data_node\n"
                + " FROM\n"
                + "dfs_data_node_user INNER JOIN (SELECT dfs_user.id FROM dfs_user WHERE dfs_user.token = ?) AS user"
                + " ON dfs_data_node_user.dfs_user = user.id) AS nodes\n"
                + " ON dfs_data_node.id = nodes.dfs_data_node";
        
        Query sqlQuery = getEntityManager().createNativeQuery(sql, DfsDataNode.class);
        sqlQuery.setParameter(1, token);
        
        return sqlQuery.getResultList();
    }
    
    public List<DfsDataNode> findAvailableDataNodes()
    {
        String sql = "SELECT dfs_data_node.id, dfs_data_node.address_ip, dfs_data_node.address_port\n"
                + "FROM \n"
                + "dfs_data_node\n"
//                + "WHERE dfs_data_node.connected = 1\n"
                + "ORDER BY dfs_data_node.last_access ASC";
        
        Query sqlQuery = getEntityManager().createNativeQuery(sql, DfsDataNode.class);
        
        return sqlQuery.getResultList();
    }
}
