package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.ConverteDados;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Epsodio;
import br.com.alura.screenmatch.service.CosumoApi;

import java.util.*;
import java.util.stream.Collectors;


public class Principal {

    private Scanner leitura = new Scanner(System.in);

    private CosumoApi consumo = new CosumoApi();

    private ConverteDados conversor = new ConverteDados();


    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=888b7841";

    public void exibeMenu() {
        System.out.println("Digite o nome da serie:");
        var nomeSerie = leitura.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.ObterDados(json, DadosSerie.class);


//        System.out.println(dados);

        List<DadosTemporada> temporadas = new ArrayList<>();

        for (int i = 1; i < dados.totalTemporadas(); i++) {
            json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + "&season=" + i + "&apikey=888b7841");
            DadosTemporada dadosTemporada = conversor.ObterDados(json, DadosTemporada.class);
            temporadas.add(dadosTemporada);
        }

//        temporadas.forEach(System.out::println);


//        temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));

//        List<DadosEpsodio> dadosEpsodios = temporadas.stream()
//                .flatMap(t -> t.episodios().stream())
//                .collect(Collectors.toList());
//
//        System.out.println("TOP 10 SERIES:");
//        dadosEpsodios.stream()
//                .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
//                .peek(e -> System.out.println("Primeiro filtro(N/A) " + e))
//                .sorted(Comparator.comparing(DadosEpsodio::avaliacao).reversed())
//                .peek(e -> System.out.println("Ordenação: " + e))
//                .limit(10)
//                .peek(e -> System.out.println("Limite: " + e))
//                .map(e -> e.titulo().toUpperCase())
//                .peek(e -> System.out.println("Mapeamento: " + e))
//                .forEach(System.out::println);

        List<Epsodio> epsodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream()
                        .map(d -> new Epsodio(t.numero(), d))
                ).collect(Collectors.toList());

        epsodios.forEach(System.out::println);


//        System.out.println("Digite um trecho do titulo do epsódio:");
//        var TrechoDoTitulo = leitura.nextLine();
//
//        Optional<Epsodio> episodioBuscado = epsodios.stream()
//                .filter(e -> e.getTitulo().toUpperCase().contains(TrechoDoTitulo.toUpperCase()))
//                .findFirst();
//
//        if (episodioBuscado.isPresent()) {
//            System.out.println("Epsódio encontrado");
//
//            System.out.println("Temporada: " + episodioBuscado.get().getTemporada());
//        } else {
//            System.out.println("Episódio não encontrado!");
//        }
//
//        System.out.println("Apratir de que ano você deseja ver os epsódios? ");
//        var ano = leitura.nextInt();
//
//        leitura.nextLine();
//
//        LocalDate dataBusca = LocalDate.of(ano, 1, 1);
//
//        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//
//        epsodios.stream()
//                .filter(e -> e.getDataDeLancamento() != null && e.getDataDeLancamento().isAfter(dataBusca))
//                .forEach(e -> System.out.println(
//                        " Temporada: " + e.getTemporada() +
//                                " Epsodio: " + e.getTitulo() +
//                                " Data de lançamento: " + e.getDataDeLancamento().format(formatador)
//                ));

        Map<Integer, Double> avaliacaoPorTemporada = epsodios.stream()
                .filter(e -> e.getAvaliacao() > 0.0)
                .collect(Collectors.groupingBy(Epsodio::getTemporada, Collectors.averagingDouble(Epsodio::getAvaliacao)));
        System.out.println(avaliacaoPorTemporada);

        DoubleSummaryStatistics est = epsodios.stream()
                .filter(e -> e.getAvaliacao() > 0.0)
                .collect(Collectors.summarizingDouble(Epsodio::getAvaliacao));

        System.out.println("Média: " + est.getAverage());
        System.out.println("Melhor episódio: " + est.getMax());
        System.out.println("Pior episódio: " + est.getMin());
        System.out.println("Quantidade: " + est.getCount());

    }
}
