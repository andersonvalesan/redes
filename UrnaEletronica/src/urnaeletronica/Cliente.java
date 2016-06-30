/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package urnaeletronica;

import servidor.Candidato;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Yuri
 */
public class Cliente implements Runnable{
    private String sOp;
    
    private boolean bConfirma = false;
    private boolean bLoaded = false;
    
    private Socket socketConexaoServidor;
    
    private List<Candidato> bdc;
    
    public Cliente() {
        this.sOp = "";
    }

    public String getsOp() {
        return sOp;
    }
    
    public void setsOp(String sOp) {
        this.sOp = sOp;
    }

    public synchronized boolean isbConfirma() {
        return bConfirma;
    }

    public synchronized void setbConfirma(boolean bConfirma) {
        this.bConfirma = bConfirma;
    }
    
    public synchronized boolean isbLoaded() {
        return bLoaded;
    }

    public synchronized void setbLoaded(boolean bLoaded) {
        this.bLoaded = bLoaded;
    }

    public int getiCodigoVotacao(int i){
        return bdc.get(i).getiCodigo_votacao();
    }
    
    public int getiNumVotos(int i){
        return bdc.get(i).getiNum_votos();
    }
    
    public void inciNumVotos(int i){
        bdc.get(i).setiNum_votos(getiNumVotos(i)+1);
    }
    
    public String getsNomeCandidato(int i){
        return bdc.get(i).getsNome_candidato();
    }
    
    public String getsFilePathCandidato(int i){
        return bdc.get(i).getFpathImage();
    }
    
    public String getsPartidoCandidato(int i){
        return bdc.get(i).getsPartido();
    }
    
    public int getiTamanhoLista(){
        return bdc.size();
    }
    
    @Override
    public void run() {
        try {
            // Conexão com cluster cosmos
            this.socketConexaoServidor = new Socket ("cosmos.lasdpc.icmc.usp.br", 40001);
            
            System.out.println("--- Client Online ---");
            System.out.println("--- Thread thCliente running ---");
            
            ObjectOutputStream paraServidor = 
                    new ObjectOutputStream(socketConexaoServidor.getOutputStream());
            
            // Comunicação entre Cliente-Servidor
            while(true){
                try {
                    Thread.sleep(1);
                } catch (InterruptedException ex) {}
                if(isbConfirma()){
                    System.out.println("Cliente - Enviando operação: " + 
                            Integer.parseInt(sOp) + ".");
                    paraServidor.writeInt(Integer.parseInt(sOp));
                    paraServidor.flush();
                    setbConfirma(false);
                    
                    if(Integer.parseInt(sOp) == 999){
                        System.out.println("Cliente - Carregando Lista...");
                        
                        ObjectInputStream doServidor = 
                            new ObjectInputStream(socketConexaoServidor.getInputStream());
                        bdc=(ArrayList<Candidato>) doServidor.readObject();
                        
                        this.bLoaded = true;
                        
                        System.out.println("Cliente - Lista Carregada.");
                    }
                }
            }
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
