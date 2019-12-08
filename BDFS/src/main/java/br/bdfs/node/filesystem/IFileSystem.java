package br.bdfs.node.filesystem;

import br.bdfs.exceptions.DfsException;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author ltosc
 */
public interface IFileSystem
{
    public String cd(String path) throws DfsException, IOException;
    public void cp(String srcPath, String dstPath) throws DfsException, IOException;
    public List<String> ls(String path) throws DfsException, IOException;
    public void mkdir(String path, boolean recursively) throws DfsException, IOException;
    public void mv(String srcPath, String dstPath) throws DfsException, IOException;
    public String pwd() throws DfsException, IOException;
    public void rm(String path) throws DfsException, IOException;
    public void rmdir(String path) throws DfsException, IOException;
    public List<String> sd(String... params) throws DfsException, IOException;
    public List<String> dtnd(String... params) throws DfsException, IOException;
    public void create(String sendFilePath) throws DfsException, IOException;
}
