/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidor;

import java.io.Serializable;

/**
 *
 * @author Yuri
 */
public class Candidato implements Serializable{
    private int iCodigo_votacao;
    private String sNome_candidato;
    private String sPartido;
    private int iNum_votos;
    
    private String fpathImage;

    public int getiCodigo_votacao() {
        return iCodigo_votacao;
    }
    
    public String getsNome_candidato() {
        return sNome_candidato;
    }

    public String getsPartido() {
        return sPartido;
    }

    public int getiNum_votos() {
        return iNum_votos;
    }

    public void setiNum_votos(int iNum_votos) {
        this.iNum_votos = iNum_votos;
    }
    
    public String getFpathImage() {
        return fpathImage;
    }
    
    public Candidato (int iCodigo, String sNome, String sPartido, String fpath){
        this.iCodigo_votacao = iCodigo;
        this.sNome_candidato = sNome;
        this.sPartido = sPartido;
        this.iNum_votos = 0;
        
        this.fpathImage = fpath;
    }
}
