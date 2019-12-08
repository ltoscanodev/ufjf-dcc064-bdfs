/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.bdfs.model.controller;

import br.bdfs.exceptions.InvalidPathException;
import br.bdfs.helper.PathHelper;
import br.bdfs.model.DfsFileFragment;
import java.util.List;
import javax.persistence.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author igor6
 */
@Repository
public class DfsFileFragmentJpaController extends AbstractJpaController<DfsFileFragment>
{
    
    public DfsFileFragmentJpaController() {
        super(DfsFileFragment.class);
    }
    
    public List<DfsFileFragment> getFileFragments(String token, String filePath) throws InvalidPathException
    {
        String sql = "SELECT *\n"
                + "FROM dfs_file_fragment AS frag \n"
                + "WHERE frag.file IN \n"
                + "(\n"
                + "  SELECT dfs_file.id FROM dfs_file WHERE dfs_file.path = ? AND dfs_file.directory IN "
                + "  (\n"
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
                + "  )\n"
                + "\n)"
                + "ORDER BY frag.last_access ASC\n";        
        Query sqlQuery = getEntityManager().createNativeQuery(sql, DfsFileFragment.class);
        sqlQuery.setParameter(1, filePath);
        sqlQuery.setParameter(2, token);
        sqlQuery.setParameter(3, PathHelper.previousPath(filePath));
        
        return sqlQuery.getResultList();
    }
        
    @Transactional
    public boolean deleteUserFile(String token, String filePath) throws InvalidPathException
    {
        String sql = "DELETE frag, data_frag \n"
                + "FROM dfs_file_fragment AS frag \n"
                + "JOIN dfs_file_fragment_data_node AS data_frag ON frag.id = data_frag.file_fragment \n"
                + "WHERE frag.file IN \n"
                + "(\n"
                + "  SELECT dfs_file.id FROM dfs_file WHERE dfs_file.path = ? AND dfs_file.directory IN "
                + "  (\n"
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
                + "  )\n"
                + "\n)";
        
        Query sqlQuery = getEntityManager().createNativeQuery(sql);
        sqlQuery.setParameter(1, filePath);
        sqlQuery.setParameter(2, token);
        sqlQuery.setParameter(3, PathHelper.previousPath(filePath));
        
        return (sqlQuery.executeUpdate() > 0);
    }
}
