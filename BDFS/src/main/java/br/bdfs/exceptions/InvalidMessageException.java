package br.bdfs.exceptions;

/**
 *
 * @author ltosc
 */
public class InvalidMessageException extends DfsException
{
    public InvalidMessageException() 
    {
        super("A mensagem recebida é inválida");
    }
}
