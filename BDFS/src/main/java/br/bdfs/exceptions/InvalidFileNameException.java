package br.bdfs.exceptions;

/**
 *
 * @author ltosc
 */
public class InvalidFileNameException extends DfsException
{
    public InvalidFileNameException() 
    {
        super("O nome de arquivo é inválido");
    }
}
