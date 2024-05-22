package br.com.alura.screenmatch.service;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

public class ConsultaGoogleTranslate {
    public static String obterTraducao(String texto) {
        // Inicializa a API do Google Translate
        Translate translate = TranslateOptions.getDefaultInstance().getService();

        System.out.println("Texto original: " + texto);

        // Traduz o texto para o português
        Translation translation = translate.translate(texto, Translate.TranslateOption.targetLanguage("pt"));

        System.out.println("Texto traduzido: " + translation.getTranslatedText());
        // Retorna o texto traduzido
        return translation.getTranslatedText();
    }
}
