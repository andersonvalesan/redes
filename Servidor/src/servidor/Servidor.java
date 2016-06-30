/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Yuri
 */
public class Servidor {
    private ServerSocket sRCliente;
    
    public Servidor() {
        try {
            // Cria socket do servidor para recepcao de conexao
            sRCliente = new ServerSocket(40001);
            
            System.out.println("--- Server Online ---");
            System.out.println("--- Thread thServidor running ---");
            
            // Trabalha com multithread para multiplos clientes
            while(true){
                // Conexao com Cliente
                Socket socketConexaoCliente;
                socketConexaoCliente = sRCliente.accept();
                System.out.println("--- Client connected ---");
                
                Proc proc = new Proc(socketConexaoCliente);
                proc.start();
            }
        } catch (IOException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Servidor servidor = new Servidor();
    }
}
