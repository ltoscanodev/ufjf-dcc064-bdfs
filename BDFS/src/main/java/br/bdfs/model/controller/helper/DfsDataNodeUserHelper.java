/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.bdfs.model.controller.helper;

import br.bdfs.context.AppContext;
import br.bdfs.exceptions.DfsException;
import br.bdfs.model.DfsDataNode;
import br.bdfs.model.DfsDataNodeUser;
import br.bdfs.model.DfsUser;
import br.bdfs.model.controller.DfsDataNodeUserJpaController;

/**
 *
 * @author igor6
 */
public class DfsDataNodeUserHelper {
    static void createDataNodeUser(DfsDataNode dfsDataNode, DfsUser dfsUser) throws DfsException
    {
        DfsDataNodeUserJpaController dfsDataNodeUserJpaController = AppContext.createInstance(DfsDataNodeUserJpaController.class);
        

        DfsDataNodeUser dataNodeUser = new DfsDataNodeUser();
        dataNodeUser.setDfsDataNode(dfsDataNode);
        dataNodeUser.setDfsUser(dfsUser);

        dfsDataNodeUserJpaController.create(dataNodeUser);
    }
}
