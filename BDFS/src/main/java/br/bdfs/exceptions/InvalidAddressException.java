package br.bdfs.exceptions;

/**
 *
 * @author ltosc
 */
public class InvalidAddressException extends DfsException
{
    public InvalidAddressException()
    {
        super("O endereço de rede é inválido");
    }
}