package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.*;
import br.com.alura.screenmatch.repository.SerieRepository;
import br.com.alura.screenmatch.service.CosumoApi;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;


public class Principal {

    private Scanner leitura = new Scanner(System.in);

    private CosumoApi consumo = new CosumoApi();

    private ConverteDados conversor = new ConverteDados();


    private final String ENDERECO = "https://www.omdbapi.com/?t=";

    private final String API_KEY = "&apikey=888b7841";


    @Autowired
    private SerieRepository repositorio;

    private List<Serie> series = new ArrayList<>();

    Optional<Serie> serieBusca;

    public Principal(SerieRepository repositorio) {
        this.repositorio = repositorio;
    }

    public void exibeMenu() {

        var menu = """
                 \n
                 1 - Buscar séries
                 2 - Buscar episódios
                 3 - Listar series buscadas
                 4 - Buscar séries por título 
                 5 - Buscar séries por ator  
                 6 - Buscar séries por avaliação  
                 7 - Buscar séries por categoria  
                 8 - Buscar séries por temporadas e melhor avaliação  
                 9 - Buscar séries por episódio  
                10 - Buscar séries por título e aprsentar os top 5 
                11 - Buscar episódios depois de uma data
                
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
            } else if (opcao == 4) {
                buscarEpisodioPorTitulo();
            } else if (opcao == 5) {
                buscarSeriePorAtor();
            } else if (opcao == 6) {
                buscarTop5Series();
            } else if (opcao == 7) {
                buscarSeriePorCategoria();
            } else if (opcao == 8) {
                buscarSeriePorTotalTemporadas();
            } else if (opcao == 9) {
                buscarSeriePorTrecho();
            } else if (opcao == 10) {
                topEpsodiosPorSerie();
            } else if (opcao == 11) {
                buscarEpisodiosPosData();
            } else {
                System.out.println("Opção inválida");
            }
            System.out.println(menu);
            opcao = leitura.nextInt();
            leitura.nextLine();
        }
        System.out.println("Saindo ...");
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
        return conversor.obterDados(json, DadosSerie.class);
    }


    private void buscarEpisodioPorSerie() {

        listarSerieBuscadas();
        System.out.print("Escolha uma serie pelo nome:");
        var nomeSerie = leitura.nextLine();

        Optional<Serie> serie = repositorio.findByTituloContainingIgnoreCase(nomeSerie);

        if (serie.isPresent()) {

            var serieEncontrada = serie.get();
            List<DadosTemporada> temporadas = new ArrayList<>();

            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                var json = consumo.obterDados(ENDERECO + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
                temporadas.add(dadosTemporada);

                List<Episodio> epsodios = temporadas.stream()
                        .flatMap(d -> d.episodios().stream()
                                .map(e -> new Episodio(d.numero(), e)))
                        .collect(Collectors.toList());

                serieEncontrada.setEpsodios(epsodios);
                repositorio.save(serieEncontrada);
            }
            temporadas.forEach(System.out::println);
        } else {
            System.out.printf("\nSerie não encontrada!");
        }
    }

    private void listarSerieBuscadas() {

        series = new ArrayList<>();
        series = repositorio.findAll();

        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);

    }

    private void buscarEpisodioPorTitulo() {

        System.out.print("Escolha uma serie pelo nome:");
        var nomeSerie = leitura.nextLine();
        serieBusca = repositorio.findByTituloContainingIgnoreCase(nomeSerie);

        if (serieBusca.isPresent()) {
            System.out.println("Dados da serie: " + serieBusca.get());
        } else {
            System.out.println("Serie não encontrada!");
        }
    }

    private void buscarSeriePorAtor() {
        System.out.println("Escolha um ator ou atriz para ver as séries: ");
        var nomeAtor = leitura.nextLine();

        System.out.println("Séries apartir de que avaliação: ");
        var avaliacao = leitura.nextDouble();

        List<Serie> seriesEnontradas = repositorio.findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(nomeAtor, avaliacao);

        System.out.println("Série(s) em que o ator ou atriz " + nomeAtor + " atuou: ");

        seriesEnontradas.forEach(
                s -> System.out.println(s.getTitulo() + " avaliações: " + s.getAvaliacao()
                ));
    }

    private void buscarTop5Series() {
        List<Serie> serieTop = repositorio.findTop5ByOrderByAvaliacaoDesc();

        serieTop.forEach(
                s -> System.out.println(s.getTitulo() + " avaliações: " + s.getAvaliacao()
                ));
    }

    private void buscarSeriePorCategoria() {
        System.out.println("Deseja bucsar series de qual genero/categoria? ");
        var nomeGenero = leitura.nextLine();

        Categoria categoria = Categoria.fromPortugues(nomeGenero);

        List<Serie> seriesPorCategoria = repositorio.findByGenero(categoria);

        System.out.println("Série de categoria " + nomeGenero);

        seriesPorCategoria.forEach(System.out::println);
    }

    private void buscarSeriePorTotalTemporadas() {
        System.out.println("Deseja buscar serie com até quantas temporadas? ");
        var totalTemporadas = leitura.nextInt();

        System.out.println("Deseja ver series apartir quanto de avaliação? ");
        var avaliacao = leitura.nextDouble();

        List<Serie> seriesPorTemporadasEAvaliacao = repositorio.seriesPorTemporadaEAvaliacao(totalTemporadas, avaliacao);

        System.out.println("Séries com até " + totalTemporadas + "temporadas e com as melhores avaliações são: ");

        seriesPorTemporadasEAvaliacao.forEach(
                s -> System.out.println(s.getTitulo() + " avaliação: " + s.getAvaliacao())
        );

    }

    private void buscarSeriePorTrecho() {
        System.out.println("Qual o nome do epsódio que deseja buscar?");
        var trechoEpisodio = leitura.nextLine();

        List<Episodio> episodioEncontrados = repositorio.episodioPorTrecho(trechoEpisodio);

        episodioEncontrados.forEach(e ->
                System.out.printf("Série: %s Temporada: %s - Episodio: %s\n",
                        e.getSerie().getTitulo(), e.getTemporada(),
                        e.getNumero(), e.getTitulo()
                )
        );
    }

    private void topEpsodiosPorSerie() {
        buscarEpisodioPorTitulo();
        if (serieBusca.isPresent()) {
            Serie serie = serieBusca.get();
            List<Episodio> topEpsodios = repositorio.topEpisodiosPorSerie(serie);

            topEpsodios.forEach(e ->
                    System.out.printf("Série: %s Temporada: %s - Episodio: %s Avaliação: %s avaliacao %s\n",
                            e.getSerie().getTitulo(), e.getTemporada(),
                            e.getNumero(), e.getTitulo(), e.getAvaliacao()
                    )
            );
        }
    }

    public void buscarEpisodiosPosData() {
        buscarEpisodioPorTitulo();

        if (serieBusca.isPresent()) {
            Serie serie = serieBusca.get();
            System.out.println("Dígite o ano limite de lançamento: ");
            var anoLancamento = leitura.nextInt();


            leitura.nextLine();

            List<Episodio> episodiosAno = repositorio.epsodiosPorSerieEAno(serie, anoLancamento);
            episodiosAno.forEach(System.out::println);
        }
    }

}
