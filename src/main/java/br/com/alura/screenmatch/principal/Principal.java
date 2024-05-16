package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.ConverteDados;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Serie;
import br.com.alura.screenmatch.repository.SerieRepository;
import br.com.alura.screenmatch.service.CosumoApi;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;


public class Principal {

    private Scanner leitura = new Scanner(System.in);

    private CosumoApi consumo = new CosumoApi();

    private ConverteDados conversor = new ConverteDados();


    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=888b7841";

    private List<DadosSerie> dadosSeries = new ArrayList<>();

    @Autowired
    private SerieRepository repositorio;

    public Principal(SerieRepository repositorio) {
        this.repositorio = repositorio;
    }

    public void exibeMenu() {

        var menu = """
                1 - Buscar séries
                2 - Buscar episódios
                3 - Listar series buscadas               
                0 - Sair                                 
                """;

        System.out.println(menu);
        var opcao = leitura.nextInt();
        leitura.nextLine();


        while (opcao != 0) {
            if (opcao == 1) {
                buscarSerieWeb();
            } else if (opcao == 2) {
                buscarEpisodioPorSerie();
            } else if (opcao == 3) {
                listarSerieBuscadas();
            } else if (opcao == 0) {
                System.out.println("Saindo...");
                
            } else {
                System.out.println("Opção inválida");
            }
            System.out.println(menu);
            opcao = leitura.nextInt();
            leitura.nextLine();
        }
    }

    private void buscarSerieWeb() {

        DadosSerie dados = getDadosSerie();

        Serie serie = new Serie(dados);
//        dadosSeries.add(dados);

        repositorio.save(serie);
        System.out.println(dados);
    }

    private DadosSerie getDadosSerie() {
        System.out.println("Digite o nome da série para busca");
        var nomeSerie = leitura.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        return dados;
    }

    private void buscarEpisodioPorSerie() {
        DadosSerie dadosSerie = getDadosSerie();
        List<DadosTemporada> temporadas = new ArrayList<>();

        for (int i = 1; i <= dadosSerie.totalTemporadas(); i++) {
            var json = consumo.obterDados(ENDERECO + dadosSerie.titulo().replace(" ", "+") + "&season=" + i + API_KEY);
            DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
            temporadas.add(dadosTemporada);
        }
    }

    private void listarSerieBuscadas() {

        List<Serie> series = new ArrayList<>();
        series = repositorio.findAll();

        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);

    }

}
