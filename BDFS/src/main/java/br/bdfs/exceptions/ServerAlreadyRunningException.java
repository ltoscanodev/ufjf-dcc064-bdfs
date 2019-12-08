package br.bdfs.exceptions;

/**
 *
 * @author ltosc
 */
public class ServerAlreadyRunningException extends DfsException
{
    public ServerAlreadyRunningException() 
    {
        super("O servidor já está em execução");
    }
}
