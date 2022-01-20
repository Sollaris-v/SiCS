package com.example.sics.model;

import com.example.sics.config.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;

public class Chamado implements Serializable {

    private String idChamado;
    private String dataAbertura;
    private String dataFechamento;
    private String descricaoCli;
    private String descricaoFunc;
    private String sastifacao;
    private String idCliente;
    private String idFuncionario;
    private String especialidade;
    private String estado;
    private String titulo;
    private String tipo;
    private String estado_idcli;
    private String estado_tipo;
    private String mesFechamento;
    private String mesAbertura;

    public Chamado() {

    }



    public void salvar(){

        //FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        //String idUsuario = Base64Custom.codificarBase64(autenticacao.getCurrentUser().getEmail());

        DatabaseReference firebase = ConfiguracaoFirebase.getFirebaseDatabase();
        firebase.child("chamados")
                //.child(idUsuario)
                .child(idChamado)
                .setValue(this);
    }

    public void atualizar(){
        DatabaseReference firebase = ConfiguracaoFirebase.getFirebaseDatabase();
        firebase.child("chamados")
                .child(idChamado)
                .setValue(this);
    }

    public void apagar(){
        DatabaseReference firebase = ConfiguracaoFirebase.getFirebaseDatabase();
        String key = firebase.push().getKey();
        firebase.child("chamados")
                .child(idChamado)
                .setValue(null);
    }



    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }


    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }


    public String getIdChamado() {
        return idChamado;
    }

    public void setIdChamado(String idChamado) {

        this.idChamado = idChamado;
    }

    public String getDataAbertura() {
        return dataAbertura;
    }

    public void setDataAbertura(String dataAbertura) {
        this.dataAbertura = dataAbertura;
    }

    public String getDataFechamento() {
        return dataFechamento;
    }

    public void setDataFechamento(String dataFechamento) {
        this.dataFechamento = dataFechamento;
    }

    public String getDescricaoCli() {
        return descricaoCli;
    }

    public void setDescricaoCli(String descricaoCli) {
        this.descricaoCli = descricaoCli;
    }

    public String getDescricaoFunc() {
        return descricaoFunc;
    }

    public void setDescricaoFunc(String descricaoFunc) {
        this.descricaoFunc = descricaoFunc;
    }

    public String getSastifacao() {
        return sastifacao;
    }

    public void setSastifacao(String sastifacao) {
        this.sastifacao = sastifacao;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public String getIdFuncionario() {
        return idFuncionario;
    }

    public void setIdFuncionario(String idFuncionario) {
        this.idFuncionario = idFuncionario;
    }

    public String getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getEstado_idcli() {
        return estado_idcli;
    }

    public void setEstado_idcli(String estado_idcli) {
        this.estado_idcli = estado_idcli;
    }

    public String getEstado_tipo() {
        return estado_tipo;
    }

    public void setEstado_tipo(String estado_tipo) {
        this.estado_tipo = estado_tipo;
    }

    public String getMesFechamento() {
        return mesFechamento;
    }

    public void setMesFechamento(String mesFechamento) {
        this.mesFechamento = mesFechamento;
    }

    public String getMesAbertura() {
        return mesAbertura;
    }

    public void setMesAbertura(String mesAbertura) {
        this.mesAbertura = mesAbertura;
    }
}
