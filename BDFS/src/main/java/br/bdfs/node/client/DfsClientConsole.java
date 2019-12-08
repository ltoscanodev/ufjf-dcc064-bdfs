package br.bdfs.node.client;

import br.bdfs.exceptions.DfsException;
import br.bdfs.helper.LogHelper;
import br.bdfs.helper.ObjectHelper;
import br.bdfs.protocol.DfsAddress;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author ltosc
 */
public class DfsClientConsole 
{
    private final BDFSClientNode bdfsClient;
    
    public DfsClientConsole(DfsAddress remoteAddress)
    {
        this.bdfsClient = new BDFSClientNode(remoteAddress);
    }
    
    public void start()
    {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        
        try
        {
            System.out.print("Usuário: ");
            String username = scanner.nextLine();
            
            System.out.print("Senha: ");
            String password = scanner.nextLine();
            
            System.out.println("Conectando...");
            bdfsClient.login(username, password);
            System.out.println("============================== BDFS ==============================");
            System.out.println();
            
            do
            {
                try
                {
                    System.out.print(String.format("%s@[%s]> ", username, bdfsClient.pwd()));
                    String cmd = scanner.nextLine();

                    DfsCommandParser dfsCommand = new DfsCommandParser(cmd);

                    switch (dfsCommand.getCommand()) 
                    {
                        case "CD": {
                            String path = dfsCommand.getParamList().get("PARAM_0");
                            bdfsClient.cd(path);
                            break;
                        }
                        case "CP": {
                            String lclPath = dfsCommand.getParamList().get("PARAM_0");
                            String destDataPath = dfsCommand.getParamList().get("PARAM_1");
                            bdfsClient.cp(lclPath, destDataPath);
                            break;
                        }
                        case "PWD": {
                            System.out.println(bdfsClient.pwd());
                            break;
                        }
                        case "LS": {
                            String path;

                            if (dfsCommand.getParamList().isEmpty()) {
                                path = bdfsClient.pwd();
                            } else {
                                path = dfsCommand.getParamList().get("PARAM_0");
                            }

                            List<String> dirList = bdfsClient.ls(path);
                            
                            if(!ObjectHelper.isNull(dirList))
                            {
                                for (String dir : dirList)
                                {
                                    System.out.println(dir);
                                }
                            }
                            
                            break;
                        }
                        case "MKDIR": {
                            String path = dfsCommand.getParamList().get("PARAM_0");
                            boolean recursively = dfsCommand.getParamList().containsKey("-R");

                            bdfsClient.mkdir(path, recursively);
                            break;
                        }
                        case "RM": {
                            String path = dfsCommand.getParamList().get("PARAM_0");
                            bdfsClient.rm(path);
                            break;
                        }
                        case "RMDIR": {
                            String path = dfsCommand.getParamList().get("PARAM_0");
                            bdfsClient.rmdir(path);
                            break;
                        }
                        case "SD": 
                        {                            
                            if((dfsCommand.getParamList().size() == 2) && dfsCommand.getParamList().containsKey("-N"))
                            {
                                String path = dfsCommand.getParamList().get("PARAM_0");
                                bdfsClient.sd("-N", path);
                            }
                            else if((dfsCommand.getParamList().size() == 3) && dfsCommand.getParamList().containsKey("-U"))
                            {
                                String user = dfsCommand.getParamList().get("PARAM_0");
                                String path = dfsCommand.getParamList().get("PARAM_1");
                                bdfsClient.sd("-U", user, path);
                            }
                            else if((dfsCommand.getParamList().size() == 1) && dfsCommand.getParamList().containsKey("-L"))
                            {
                                List<String> sharedDirList = bdfsClient.sd("-L");
                                
                                for(String sharedDir : sharedDirList)
                                {
                                    System.out.println(sharedDir);
                                }
                            }
                            else
                            {
                                System.out.println("Parâmetros inválidos");
                            }
                            break;
                        }
                        case "DTND":
                            if((dfsCommand.getParamList().size() == 3) && dfsCommand.getParamList().containsKey("-N"))
                            {
                                String dataNodeIp = dfsCommand.getParamList().get("PARAM_0");
                                String dataNodePort = dfsCommand.getParamList().get("PARAM_1");
                                bdfsClient.dtnd("-N", dataNodeIp, dataNodePort);
                            }
                            else if((dfsCommand.getParamList().size() == 1) && dfsCommand.getParamList().containsKey("-L"))
                            {
                                List<String> dataNodeList = bdfsClient.dtnd("-L");
                                
                                for(String dataNode : dataNodeList)
                                {
                                    System.out.println(dataNode);
                                }
                            }
                            else
                            {
                                System.out.println("Parâmetros inválidos");
                            }
                            break;
                        case "CREATE":
                            if(dfsCommand.getParamList().size() == 1)
                            {
                                String sendFilePath = dfsCommand.getParamList().get("PARAM_0");
                                bdfsClient.create(sendFilePath);
                            }
                            else
                            {
                                System.out.println("Parâmetros inválidos");
                            }
                            break;
                        case "MV": {
                            String orgDataPath = dfsCommand.getParamList().get("PARAM_0");
                            String destPath = dfsCommand.getParamList().get("PARAM_1");
                            bdfsClient.mv(orgDataPath, destPath);
                            break;
                        }
                        case "HELP":
                            System.out.println("=============AJUDA================================================");
                            System.out.println("mkdir <caminho>");
                            System.out.println("   -Cria um diretório no local especificado.");
                            System.out.println("cd <caminho>(..)");
                            System.out.println("   -Movimenta o cliente através da árvore de diretórios. Utilizar");
                            System.out.println(" .. indica diretório acima");
                            System.out.println("cp <caminho_local> <caminho_arquivo_remoto>");
                            System.out.println("   -Copia o arquivo para o caminho desejado pelo Usuário.");
                            System.out.println("pwd");
                            System.out.println("   -Indica em qual diretório o cliente está.");
                            System.out.println("ls");
                            System.out.println("   -Lista os diretórios e arquivos do diretório atual.");
                            System.out.println("rm <arquivo>");
                            System.out.println("   -Remove o arquivo no diretório atual.");
                            System.out.println("rmdir <caminho>");
                            System.out.println("   -Remove o diretório selecionado");
                            System.out.println("sd -l");
                            System.out.println("   -Lista os diretórios compartilhados.");
                            System.out.println("sd -u <nome_do_usuário> <nome_diretório>");
                            System.out.println("   -Lista os diretórios compartilhados.");
                            System.out.println("sd -n <nome_diretorio>");
                            System.out.println("   -Lista os diretórios compartilhados.");
                            System.out.println("dtnd -n <ip> <porta>");
                            System.out.println("   -Cria registro do nó de dados.");
                            System.out.println("create <nome_arquivo_origem>");
                            System.out.println("   -Carrega arquivo para o diretório atual.");
                            System.out.println("mv <caminho_arquivo_origem> <caminho_destino>");
                            System.out.println("   -Move o arquivo para um outro diretório.");
                            break;
                        case "EXIT":
                            running = false;
                            break;
                        default:
                            System.out.println("Comando desconhecido");
                    }
                }
                catch (DfsException | IOException ex) 
                {
                    System.out.println(ex.getMessage());
                }
            }
            while(running);
            
            System.out.println();
            System.out.println("==================================================================");
            System.out.println("Desconectando...");
            bdfsClient.logout();
        }
        catch (DfsException | IOException ex) 
        {
            LogHelper.logError(ex.getMessage());
        }
    }
}
