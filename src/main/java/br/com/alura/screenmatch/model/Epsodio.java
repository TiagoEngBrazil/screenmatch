package br.com.alura.screenmatch.model;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class Epsodio {
    private Integer temporada;

    private String titulo;
    private Integer numero;

    private Double avaliacao;

    private LocalDate dataDeLancamento;

    public Epsodio(Integer numeroTemporada, DadosEpsodio dadosEpsodio) {
        this.temporada = numeroTemporada;
        this.titulo = dadosEpsodio.titulo();
        this.numero = dadosEpsodio.numero();

        try {
            this.avaliacao = Double.valueOf(dadosEpsodio.avaliacao());
        } catch (NumberFormatException e) {
            this.avaliacao = 0.0;
        }

        try {
            this.dataDeLancamento = LocalDate.parse(dadosEpsodio.dataDeLancamento());
        } catch (DateTimeParseException e) {
            this.dataDeLancamento = null;
        }


    }

    public Integer getTemporada() {
        return temporada;
    }

    public void setTemporada(Integer temporada) {
        this.temporada = temporada;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public Double getAvaliacao() {
        return avaliacao;
    }

    public void setAvaliacao(Double avaliacao) {
        this.avaliacao = avaliacao;
    }

    public LocalDate getDataDeLancamento() {
        return dataDeLancamento;
    }

    public void setDataDeLancamento(LocalDate dataDeLancamento) {
        this.dataDeLancamento = dataDeLancamento;
    }

    @Override
    public String toString() {
        return "temporada=" + temporada +
                ", titulo='" + titulo + '\'' +
                ", numero=" + numero +
                ", avaliacao=" + avaliacao +
                ", dataDeLancamento=" + dataDeLancamento;
    }
}
