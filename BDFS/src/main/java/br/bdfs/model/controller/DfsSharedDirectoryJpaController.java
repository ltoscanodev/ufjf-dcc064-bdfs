package br.bdfs.model.controller;

import br.bdfs.model.DfsSharedDirectory;
import org.springframework.stereotype.Repository;

/**
 *
 * @author ltosc
 */
@Repository
public class DfsSharedDirectoryJpaController extends AbstractJpaController<DfsSharedDirectory>
{
    public DfsSharedDirectoryJpaController() 
    {
        super(DfsSharedDirectory.class);
    }
}
