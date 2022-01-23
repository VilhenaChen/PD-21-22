package pd.tp.rmi;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Scanner;

public class AppRMI extends UnicastRemoteObject implements InterfaceAppRMI{
    protected AppRMI() throws RemoteException {
    }

    public static void main(String[] args) {
        System.out.println("À procura do RMI do GRDS....");
        try {
            Registry r = LocateRegistry.getRegistry("127.0.0.1", Registry.REGISTRY_PORT);
            InterfaceGestaoRMI interfaceGestaoRMI = (InterfaceGestaoRMI) r.lookup("GRDS_Service");
            AppRMI appRMI = new AppRMI();
            r.rebind("GRDS_Service",appRMI);
            System.out.println("À espera de um observer....");
            Thread.sleep(10000);
            int op;
            Scanner scanner = new Scanner(System.in);
            do {
                System.out.println("----------Menu---------");
                System.out.println("1- Listar servers");
                System.out.println("0- Sair");
                op=scanner.nextInt();
                if(op==1){
                    interfaceGestaoRMI.pedirInformacaoServidores(appRMI);
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
}
