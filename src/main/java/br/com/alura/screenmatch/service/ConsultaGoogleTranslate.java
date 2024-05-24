package br.com.alura.screenmatch.service;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;


// TODO
public class ConsultaGoogleTranslate {
    public static String obterTraducao(String texto) {

        Translate translate = TranslateOptions.getDefaultInstance().getService();

        System.out.println("Texto original: " + texto);


        Translation translation = translate.translate(texto, Translate.TranslateOption.targetLanguage("pt"));

        System.out.println("Texto traduzido: " + translation.getTranslatedText());

        return translation.getTranslatedText();
    }
}
