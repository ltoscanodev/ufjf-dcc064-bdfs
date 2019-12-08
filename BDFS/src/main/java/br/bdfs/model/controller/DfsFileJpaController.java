/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.bdfs.model.controller;

import br.bdfs.exceptions.InvalidPathException;
import br.bdfs.helper.PathHelper;
import br.bdfs.model.DfsFile;
import java.util.List;
import javax.persistence.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author igor6
 */
@Repository
public class DfsFileJpaController extends AbstractJpaController<DfsFile>
{
    
    public DfsFileJpaController() {
        super(DfsFile.class);
    }
    
    public List<DfsFile> getChildFiles(String token, String parentPath)
    {
        String sql = "SELECT \n"
                + "	child_file.id, child_file.name, child_file.extension, child_file.path, \n"
                + "	child_file.creation_time, child_file.modification_time, child_file.access_time, \n"
                + "	child_file.locked, child_file.locked, child_file.directory, child_file.size \n"
                + "FROM \n"
                + "(SELECT * FROM dfs_file WHERE dfs_file.directory = (SELECT id FROM dfs_directory WHERE dfs_directory.path = ?)) AS child_file,\n"
                + "(SELECT dfs_user.id, dfs_directory.path FROM dfs_user INNER JOIN dfs_directory ON (dfs_user.home_directory = dfs_directory.id) WHERE dfs_user.token = ?) AS home_dir\n"
                + "LEFT JOIN(\n"
                + "	SELECT dfs_user_shared_directory.user, dfs_directory.path FROM dfs_shared_directory\n"
                + "	INNER JOIN dfs_user_shared_directory ON dfs_shared_directory.id = dfs_user_shared_directory.shared_directory\n"
                + "	INNER JOIN dfs_directory ON dfs_directory.id = dfs_shared_directory.shared_directory\n"
                + ") AS shared_dir ON shared_dir.user = home_dir.id\n"
                + "WHERE (child_file.path LIKE concat(home_dir.path, '/%')) OR (child_file.path LIKE concat(shared_dir.path, '/%'))";
        
        Query sqlQuery = getEntityManager().createNativeQuery(sql, DfsFile.class);
        sqlQuery.setParameter(1, parentPath);
        sqlQuery.setParameter(2, token);
        
        return sqlQuery.getResultList();
    }
    
    public DfsFile getFile(String token, String filePath) throws InvalidPathException
    {
        String sql = "SELECT \n"
                + "	dfs_file.*"
                + "FROM \n"
                + "dfs_file,\n"
                + "(SELECT dfs_user.id, dfs_directory.path FROM dfs_user INNER JOIN dfs_directory ON (dfs_user.home_directory = dfs_directory.id) WHERE dfs_user.token = ?) AS home_dir\n"
                + "LEFT JOIN(\n"
                + "	SELECT dfs_user_shared_directory.user, dfs_directory.path FROM dfs_shared_directory\n"
                + "	INNER JOIN dfs_user_shared_directory ON dfs_shared_directory.id = dfs_user_shared_directory.shared_directory\n"
                + "	INNER JOIN dfs_directory ON dfs_directory.id = dfs_shared_directory.shared_directory\n"
                + ") AS shared_dir ON shared_dir.user = home_dir.id\n"
                + "WHERE ((dfs_file.path LIKE concat(home_dir.path, '/%')) OR (dfs_file.path LIKE concat(shared_dir.path, '/%')) AND dfs_file.path = ?)";
        
        Query sqlQuery = getEntityManager().createNativeQuery(sql, DfsFile.class);
        sqlQuery.setParameter(1, token);
        sqlQuery.setParameter(2,filePath);
        
        return (DfsFile) sqlQuery.getSingleResult();
    }
    
    @Transactional
    public boolean deleteUserFile(String token, String filePath) throws InvalidPathException
    {
        String sql = "DELETE FROM dfs_file\n"
                + "WHERE dfs_file.path = ? AND dfs_file.directory IN\n"
                + "(\n"
                + "	SELECT * FROM\n"
                + "    (\n"
                + "	   SELECT dfs_directory.id \n"
                + "        FROM \n"
                + "		dfs_directory,\n"
                + "		(SELECT dfs_user.id, dfs_directory.path FROM dfs_user INNER JOIN dfs_directory ON dfs_directory.id = dfs_user.home_directory WHERE dfs_user.token = ?) AS home_dir\n"
                + "		LEFT JOIN\n"
                + "            (\n"
                + "			SELECT dfs_user_shared_directory.user, dfs_directory.path FROM dfs_shared_directory\n"
                + "			INNER JOIN dfs_user_shared_directory ON dfs_shared_directory.id = dfs_user_shared_directory.shared_directory\n"
                + "			INNER JOIN dfs_directory ON dfs_directory.id = dfs_shared_directory.shared_directory\n"
                + "			) AS shared_dir ON shared_dir.user = home_dir.id\n"
                + "		WHERE ((dfs_directory.path LIKE home_dir.path OR dfs_directory.path LIKE concat(home_dir.path, '/%')) \n"
                + "				OR (dfs_directory.path LIKE shared_dir.path OR dfs_directory.path LIKE concat(shared_dir.path, '/%')))\n"
                + "				AND dfs_directory.path = ?\n"
                + "	) AS del_dir\n"
                + ")\n"
                + "LIMIT 1";
        
        Query sqlQuery = getEntityManager().createNativeQuery(sql);
        sqlQuery.setParameter(1, filePath);
        sqlQuery.setParameter(2, token);
        sqlQuery.setParameter(3, PathHelper.previousPath(filePath));
        
        return (sqlQuery.executeUpdate() > 0);
    }
}
