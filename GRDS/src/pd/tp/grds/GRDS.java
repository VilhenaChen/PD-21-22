package pd.tp.grds;

import pd.tp.grds.servidor.Servidores;
import pd.tp.grds.threads.ThreadHeartbeatServidores;
import pd.tp.grds.threads.ThreadIniciaComunicacao;
import pd.tp.grds.threads.ThreadMulticastServidores;
import pd.tp.rmi.GestaoRMI;

import java.io.IOException;
import java.net.*;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;
import java.util.Timer;

import static java.rmi.server.UnicastRemoteObject.unexportObject;

public class GRDS {
    private static final int PORTO = 9001;
    private ThreadMulticastServidores threadMulticastServidores;
    private Scanner scanner;
    private MulticastSocket ms;
    private InetSocketAddress isa;
    private NetworkInterface ni;
    private ThreadHeartbeatServidores threadHeartbeatServidores;
    private Timer timer;


    private void IniciaThreadMulticast(){
        try {
            ms = new MulticastSocket(3030);
            InetAddress ia = InetAddress.getByName("230.30.30.30");
            InetAddress myIa = InetAddress.getByName(InetAddress.getLocalHost().getHostAddress());
            isa = new InetSocketAddress(ia, 3030);
            ni = NetworkInterface.getByInetAddress(myIa);
            ms.joinGroup(isa,ni);
            threadMulticastServidores = new ThreadMulticastServidores(myIa.getHostAddress(),String.valueOf(PORTO),ms, ia);
            threadMulticastServidores.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        //GRDS grds = new GRDS();
        Servidores servidores = new Servidores();
        DatagramSocket ds = new DatagramSocket(PORTO);
        GestaoRMI gestaoRMI = null;
        GRDS grds = new GRDS();
        //RMI

        try{
            Registry rA = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
            gestaoRMI = new GestaoRMI(servidores);
            rA.rebind("GRDS_Service",gestaoRMI);
        }catch (RemoteException e){
            e.printStackTrace();
        }

        //RMI


        System.out.println("---- GRDS INICIADO ----");
        grds.threadHeartbeatServidores = new ThreadHeartbeatServidores(servidores,gestaoRMI);
        grds.timer = new Timer("VerifyHeartbeat");
        grds.timer.schedule(grds.threadHeartbeatServidores, 0, 1000);
        ThreadIniciaComunicacao threadIniciaComunicacao = new ThreadIniciaComunicacao(servidores,ds,gestaoRMI);
        threadIniciaComunicacao.start();
        grds.IniciaThreadMulticast();
        grds.scanner = new Scanner(System.in);
        while(true){
            System.out.println("Insira 'quit' para sair");
            String comando = grds.scanner.nextLine();
            if(comando.equals("quit")){
                break;
            }
        }

        servidores.informaServidoresdoEncerramento(ds);

        if(gestaoRMI!=null){
            unexportObject(gestaoRMI,true);
        }
        threadIniciaComunicacao.interrupt();
        grds.threadMulticastServidores.interrupt();
        grds.threadHeartbeatServidores.cancel();
        grds.timer.cancel();
        grds.timer.purge();

        grds.ms.leaveGroup(grds.isa,grds.ni);
        grds.ms.close();
        ds.close();

        System.out.println("GRDS terminado");
    }
}
