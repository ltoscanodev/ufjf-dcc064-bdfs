/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.bdfs.model.controller;

import br.bdfs.model.DfsDataNodeUser;
import org.springframework.stereotype.Repository;

/**
 *
 * @author igor6
 */
@Repository
public class DfsDataNodeUserJpaController extends AbstractJpaController<DfsDataNodeUser> 
{
    
    public DfsDataNodeUserJpaController() {
        super(DfsDataNodeUser.class);
    }
    
}
