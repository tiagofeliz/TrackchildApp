package com.example.example.instanceofworries.Entity;

import java.io.Serializable;

/**
 * Created by THANATHOS on 08/03/2017.
 */

public class Area implements Serializable {
    private int cdArea;
    private int flag;
    private String identificacao;
    private int cdOrganizacao;
    private boolean ativo;
    private String risco;
    private String UUID;
    private String major;
    private String minor;
    private String tipo;
    private String rssi;
    private String txpower;
    private String orgname;
    private String distancia;
    private String status;
    private String raio;

    public String getRaio() {
        return raio;
    }

    public void setRaio(String raio) {
        this.raio = raio;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDistancia() {
        return distancia;
    }

    public void setDistancia(String distancia) {
        this.distancia = distancia;
    }

    public String getOrgname() {
        return orgname;
    }

    public void setOrgname(String orgname) {
        this.orgname = orgname;
    }

    public String getRssi() {
        return rssi;
    }

    public void setRssi(String rssi) {
        this.rssi = rssi;
    }

    public String getTxpower() {
        return txpower;
    }

    public void setTxpower(String txpower) {
        this.txpower = txpower;
    }

    public int getCdArea() {
        return cdArea;
    }

    public void setCdArea(int cdArea) {
        this.cdArea = cdArea;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getIdentificacao() {
        return identificacao;
    }

    public void setIdentificacao(String identificacao) {
        this.identificacao = identificacao;
    }

    public int getCdOrganizacao() {
        return cdOrganizacao;
    }

    public void setCdOrganizacao(int cdOrganizacao) {
        this.cdOrganizacao = cdOrganizacao;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getMinor() {
        return minor;
    }

    public void setMinor(String minor) {
        this.minor = minor;
    }

    public String getRisco() {
        return risco;
    }

    public void setRisco(String risco) {
        this.risco = risco;
    }
}
