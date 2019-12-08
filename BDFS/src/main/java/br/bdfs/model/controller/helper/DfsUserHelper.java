package br.bdfs.model.controller.helper;

import br.bdfs.context.AppContext;
import br.bdfs.exceptions.AuthException;
import br.bdfs.helper.ObjectHelper;
import br.bdfs.helper.TokenHelper;
import br.bdfs.model.DfsDirectory;
import br.bdfs.model.DfsUser;
import br.bdfs.model.controller.DfsUserJpaController;
import javax.persistence.NoResultException;

/**
 *
 * @author ltosc
 */
public class DfsUserHelper 
{
    public static DfsUser findByUserName(String username) throws AuthException
    {
        try
        {
            DfsUserJpaController dfsUserJpaController = AppContext.createInstance(DfsUserJpaController.class);
            return dfsUserJpaController.findByNamedQuerySingle("DfsUser.findByUsername", "username", username);
        }
        catch (NoResultException ex) 
        {
            throw new AuthException("Usuário não encontrado");
        }
    }
    
    public static DfsUser findUserByToken(String token) throws AuthException
    {
        try
        {
            DfsUserJpaController dfsUserJpaController = AppContext.createInstance(DfsUserJpaController.class);
            return dfsUserJpaController.findByNamedQuerySingle("DfsUser.findByToken", "token", token);
        }
        catch (NoResultException ex) 
        {
            throw new AuthException("Usuário não encontrado");
        }
    }
    
    public static boolean isLogged(String token)
    {
        try 
        {
            return (findUserByToken(token) != null);
        } 
        catch (AuthException ex) 
        {
            return false;
        }
    }
    
    public static DfsDirectory getUserHomeDirectory(String token) throws AuthException
    {
        try
        {
            DfsUserJpaController dfsUserJpaController = AppContext.createInstance(DfsUserJpaController.class);
            DfsUser user = dfsUserJpaController.findByNamedQuerySingle("DfsUser.findByToken", "token", token);
            
            return user.getHomeDirectory();
        }
        catch (NoResultException ex) 
        {
            throw new AuthException("Usuário não logado");
        }
    }
    
    public static String login(String username, String password) throws AuthException
    {
        try
        {
            DfsUserJpaController dfsUserJpaController = AppContext.createInstance(DfsUserJpaController.class);
            DfsUser user = dfsUserJpaController.findByNamedQuerySingle("DfsUser.findByUsername", "username", username);
            
            if(!ObjectHelper.isNull(user.getToken()))
            {
                return user.getToken();
            }
            else
            {
                if (user.getPassword().equalsIgnoreCase(password)) 
                {
                    String userToken = TokenHelper.generate();
                    user.setToken(userToken);
                    dfsUserJpaController.edit(user);

                    return userToken;
                } 
                else 
                {
                    throw new AuthException("Senha incorreta");
                }
            }
        }
        catch (NoResultException ex) 
        {
            throw new AuthException("Usuário não encontrado");
        }
    }
    
    public static void logout(String token) throws AuthException
    {
        try
        {
            DfsUserJpaController dfsUserJpaController = AppContext.createInstance(DfsUserJpaController.class);
            DfsUser user = dfsUserJpaController.findByNamedQuerySingle("DfsUser.findByToken", "token", token);
            user.setToken(null);
            dfsUserJpaController.edit(user);
        }
        catch (NoResultException ex) 
        {
            throw new AuthException("Usuário não logado");
        }
    }
}
