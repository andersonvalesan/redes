/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
public class Proc extends Thread {
    private int iOperacao;
    private boolean listDownloaded = false;
    
    private List<Candidato> bdc = new ArrayList<Candidato>();
    private File fLista = new File("lista.txt");
    private final Socket socketConexaoCliente;
    
    
    public Proc (Socket scc) {
        this.socketConexaoCliente = scc;
    }
    
    // Cria novos candidatos e coloca na nova lista
    private void novaListaCandidato() {
        bdc.add(new Candidato(13666, "Alisson", "PT", "/Alisson.jpg"));

        bdc.add(new Candidato(24240, "Luis", "PSOL", "/Luis.jpg"));

        bdc.add(new Candidato(44444, "Anderson", "PSDB", "/Anderson.jpg"));
        
        bdc.add(new Candidato(30303, "Guilherme", "PV", "/Guilherme.jpg"));
    }
    
    public synchronized boolean isListDownloaded() {
        return listDownloaded;
    }

    public synchronized void setListDownloaded(boolean listDownloaded) {
        this.listDownloaded = listDownloaded;
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
            ObjectInputStream doCliente = 
                    new ObjectInputStream(socketConexaoCliente.getInputStream());
            
            // Comunicação entre Servidor-Cliente
            while(true) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException ex) {}
                
                // Recebendo operacao do cliente
                this.iOperacao = doCliente.readInt();
                System.out.println("Servidor - Recebendo operação: " + this.iOperacao + ".");
                
                // Voto em branco
                if(this.iOperacao == 2){
                    int iMaiorValor = 0;
                    int iMaiorIndex = 0;
                    
                    // Procura o candidato com mais votos
                    for(int i=0; i<this.getiTamanhoLista(); i++){
                        if(iMaiorValor < this.getiNumVotos(i)){
                            iMaiorValor = this.getiNumVotos(i);
                            iMaiorIndex = i;
                        }
                    }
                    
                    // Incrementa os votos do candidato
                    if(iMaiorValor != 0){
                        this.inciNumVotos(iMaiorIndex);
                    }
                    
                    for(int i=0; i<this.getiTamanhoLista(); i++){
                        System.out.println(this.getsNomeCandidato(i) + ": " +
                                this.getiNumVotos(i));
                    }
                }
                
                // Voto nulo
                else if(this.iOperacao == 3){
                    for(int i=0; i<this.getiTamanhoLista(); i++){
                        System.out.println(this.getsNomeCandidato(i) + ": " +
                                this.getiNumVotos(i));
                    }
                }
                
                // Finalizando votacao na urna
                else if(this.iOperacao == 888){
                    ObjectOutputStream paraArquivo = 
                        new ObjectOutputStream(new FileOutputStream(fLista));
                    paraArquivo.writeObject(this.bdc);
                    paraArquivo.close();

                    this.bdc.clear();

                    System.out.println("Servidor - Votação finalizada com sucesso!");
                }
                
                // Carregando lista de candidatos no servidor
                else if(this.iOperacao == 999){
                    System.out.println("Servidor - Carregando Lista...");
            
                    if(fLista.exists()){
                        ObjectInputStream doArquivo =
                            new ObjectInputStream(new FileInputStream(fLista));
                        
                        bdc = (List<Candidato>) doArquivo.readObject();
                    }
                    else{
                        this.novaListaCandidato();
                    }
                    ObjectOutputStream paraCliente = 
                        new ObjectOutputStream(socketConexaoCliente.getOutputStream());
                            
                    paraCliente.writeObject(bdc);
                    
                    this.listDownloaded = true;
                    
                    System.out.println("Servidor - Lista Carregada.");
                }
                
                // Voto em candidato
                else{
                    for(int i=0; i<this.getiTamanhoLista(); i++){
                        if(this.iOperacao == this.getiCodigoVotacao(i))
                            this.inciNumVotos(i);
                    }
                    for(int i=0; i<this.getiTamanhoLista(); i++){
                        System.out.println(this.getsNomeCandidato(i) + ": " +
                                this.getiNumVotos(i));
                    }
                }
            }
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
