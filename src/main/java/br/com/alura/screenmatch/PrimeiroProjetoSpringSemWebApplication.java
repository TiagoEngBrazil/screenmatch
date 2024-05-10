package br.com.alura.screenmatch;

import br.com.alura.screenmatch.model.ConverteDados;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.service.CosumoApi;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PrimeiroProjetoSpringSemWebApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(PrimeiroProjetoSpringSemWebApplication.class, args);
    }


    @Override
    public void run(String... args) throws Exception {
        var consumoApi = new CosumoApi();
		var json = consumoApi.obterDados("https://www.omdbapi.com/?t=lost&Season=1&apikey=888b7841");

        System.out.println(json);
		ConverteDados conversor = new ConverteDados();

		DadosSerie dados = conversor.ObterDados(json, DadosSerie.class);
		System.out.println(dados);
    }



}
