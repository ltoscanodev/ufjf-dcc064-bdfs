package br.bdfs.model.controller;

import br.bdfs.model.DfsUser;
import org.springframework.stereotype.Repository;

/**
 *
 * @author ltosc
 */
@Repository
public class DfsUserJpaController extends AbstractJpaController<DfsUser>
{
    public DfsUserJpaController()
    {
        super(DfsUser.class);
    }
}

