package pd.tp.rmi;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Scanner;

public class AppRMI extends UnicastRemoteObject implements InterfaceAppRMI{
    private static InterfaceGestaoRMI interfaceGestaoRMI;
    protected AppRMI() throws RemoteException {
    }

    public static void main(String[] args) {
        System.out.println("À procura do RMI do GRDS....");
        try {
            Registry r = LocateRegistry.getRegistry("127.0.0.1", Registry.REGISTRY_PORT);

            AppRMI appRMI = new AppRMI();
            interfaceGestaoRMI = (InterfaceGestaoRMI) r.lookup("GRDS_Service");
            r.rebind("GRDS_Service",appRMI);
            System.out.println("À espera de um observer....");
            Thread.sleep(10000);
            int op;
            Scanner scanner = new Scanner(System.in);
            do {
                System.out.println("----------Menu---------");
                System.out.println("1- Listar servers");
                System.out.println("2- Subscrever as Informações sobre Clientes do GRDS");
                System.out.println("3- Cancelar a Subscrição de Informações sobre Clientes do GRDS");
                System.out.println("4- Subscrever as Informações sobre Servidores do GRDS");
                System.out.println("5- Cancelar a Subscrição de Informações sobre Servidores do GRDS");
                System.out.println("6- Subscrever as Informações sobre Notificações do GRDS");
                System.out.println("7- Cancelar a Subscrição de Informações sobre Notificações do GRDS");
                System.out.println("8- Subscrever todas as Informações do GRDS");
                System.out.println("9- Cancelar a Subscrição de todas as Informações do GRDS");
                System.out.println("0- Sair");
                op=scanner.nextInt();
                switch (op){
                    case 1:
                        interfaceGestaoRMI.pedirInformacaoServidores(appRMI);
                        break;
                    case 2:
                        interfaceGestaoRMI.addNovoListenerClientes(appRMI);
                        break;
                    case 3:
                        interfaceGestaoRMI.removeNovoListenerClientes(appRMI);
                        break;
                    case 4:
                        interfaceGestaoRMI.addNovoListenerServidores(appRMI);
                        break;
                    case 5:
                        interfaceGestaoRMI.removeNovoListenerServidores(appRMI);
                        break;
                    case 6:
                        interfaceGestaoRMI.addNovoListenerNotificacoes(appRMI);
                        break;
                    case 7:
                        interfaceGestaoRMI.removeNovoListenerNotificacoes(appRMI);
                        break;
                    case 8:
                        interfaceGestaoRMI.addNovoListenerClientes(appRMI);
                        interfaceGestaoRMI.addNovoListenerServidores(appRMI);
                        interfaceGestaoRMI.addNovoListenerNotificacoes(appRMI);
                        break;
                    case 9:
                        interfaceGestaoRMI.removeNovoListenerClientes(appRMI);
                        interfaceGestaoRMI.removeNovoListenerServidores(appRMI);
                        interfaceGestaoRMI.removeNovoListenerNotificacoes(appRMI);
                        break;
                    default:
                        break;
                }
            }while (op!=0);
            unexportObject(appRMI,true);

        } catch (RemoteException | NotBoundException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void imprimeServidores(ArrayList<String> servidores) throws RemoteException {
        for (String server : servidores){
            System.out.println(server);
        }
    }

    @Override
    public void novoCliente() throws RemoteException {
        System.out.println("NOTIFICAÇÂO: Um Cliente entrou em contacto com o GRDS");
    }

    @Override
    public void novoServidor(int id) throws RemoteException {
        System.out.println("NOTIFICAÇÂO: Foi inserido um novo servidor ao GRDS com o ID: " + id);
    }

    @Override
    public void eliminacaoServidor(int id) throws RemoteException {
        System.out.println("NOTIFICAÇÂO: Foi eliminado um novo servidor do GRDS com o ID: " + id);
    }

    @Override
    public void notificacao(String notificacao) throws RemoteException {
        System.out.println("NOTIFICAÇÂO: O GRDS recebeu a seguinte notificação: " + notificacao);
    }
}
