package br.com.alura.screenmatch.service;

import br.com.alura.screenmatch.service.ConsultaChatGPT;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConsultaChatGPTTest {

    @Test
    public void testObterTraducao() {
        String texto = "Hello, world!";
        String traducao = ConsultaChatGPT.obterTraducao(texto);
        assertEquals("Olá, mundo!", traducao);
    }
}
