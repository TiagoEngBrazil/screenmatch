package br.com.alura.screenmatch.service;

public interface IconverteDados {
    <T> T ObterDados(String json, Class<T> classe);


}
