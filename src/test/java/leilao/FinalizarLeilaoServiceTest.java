package leilao;

import br.com.alura.leilao.dao.LeilaoDao;
import br.com.alura.leilao.model.Lance;
import br.com.alura.leilao.model.Leilao;
import br.com.alura.leilao.model.Usuario;
import br.com.alura.leilao.service.EnviadorDeEmails;
import br.com.alura.leilao.service.FinalizarLeilaoService;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class FinalizarLeilaoServiceTest {

    private FinalizarLeilaoService service;

    @Mock
    private LeilaoDao leilaoDao;

    @Mock
    private EnviadorDeEmails enviadorDeEmails;


    @BeforeEach
    public void beforeEach() {
        MockitoAnnotations.initMocks(this);//para ler anotacoes do mockito e inicializar
        this.service = new FinalizarLeilaoService(leilaoDao, enviadorDeEmails);
    }

    @Test
    void deveriaFinalizarUmLeilao() {
        List<Leilao> leiloes = leiloes();
        Mockito.when(leilaoDao.buscarLeiloesExpirados()).thenReturn(leiloes);//“Mockito, quando, when, esse método do mock for chamado, devolva isso”,

        service.finalizarLeiloesExpirados();

        Leilao leilao = leiloes.get(0);//. Essa lista só tem um único leilão. Vou guardar numa variável
        Assert.assertTrue(leilao.isFechado());// testa se o leilao fechou, ou seja, tem que esta vaelndo true
        Assert.assertEquals(new BigDecimal("900"), leilao.getLanceVencedor().getValor());//verifica se o lance vencedor foi de valor 900
        Mockito.verify(leilaoDao).salvar(leilao);//verifica se o metodo salvar foi executado
    }

    //novo cenario de test
    @Test
    void deveriaEnviarEmailParaVencedorDoLeilao() {
        List<Leilao> leiloes = leiloes(); //Lista Leilao com 2 Lances

        Mockito.when(leilaoDao.buscarLeiloesExpirados()).thenReturn(leiloes);//“Mockito, quando, when, esse método do mock for chamado, devolva isso”,

        service.finalizarLeiloesExpirados();

        Leilao leilao = leiloes.get(0);//. Essa lista só tem um único leilão. Vou guardar numa variável
        Lance lanceVencendor = leilao.getLanceVencedor(); // armazena o lance vencedor em uma variavel
        Mockito.verify(enviadorDeEmails).enviarEmailVencedorLeilao(lanceVencendor);//verifica se o metodo salvar foi executado, sem precisar acessar o banco de dados
    }
//novo cenario - simula exception
    @Test
    void naoDeveriaEnviarEmailParaVencendorDoLeilaoEmCasoDeErroAoEncerrarOLeilao() {
        List<Leilao> leiloes = leiloes(); //Lista Leilao com 2 Lances

        Mockito.when(leilaoDao.buscarLeiloesExpirados()).thenReturn(leiloes);//“Mockito, quando, when, esse método do mock for chamado, devolva isso”,

        Mockito.when(leilaoDao.salvar(Mockito.any())).thenThrow(RuntimeException.class);//sempre que o metodo salvar for chamado, com qualquer parametro, lancar uma exception
        try {
            service.finalizarLeiloesExpirados();
            Mockito.verifyNoInteractions(enviadorDeEmails);//"MOCKITO,verifica se houve alguma chamada para o enviador de emails". Verifica  se um metodo NAO foi chamado, NORMALMENTE UTILIZADO PARA TESTAR EXCEPTIONS

        } catch (Exception e) {
        }

    }

    private List<Leilao> leiloes() {
        List<Leilao> lista = new ArrayList<>();

        Leilao leilao = new Leilao("Celular", new BigDecimal("500"), new Usuario("Fulano"));

        Lance primeiro = new Lance(new Usuario("Beltrano"), new BigDecimal("600"));
        Lance segundo = new Lance(new Usuario("Ciclano"), new BigDecimal("900"));

        leilao.propoe(primeiro);
        leilao.propoe(segundo);

        lista.add(leilao);

        return lista;
    }
}


/**
 *  Vou clicar na classe finalizaLeilaoService, "Ctrl + N", JUnit, escolher a opção JUnit test case.
 *  E aí ele já preencheu tudo certinho, só vou dar um finish.
 *  Está aqui nossa classe de teste vazia, vou só apagar essa implementação
 *  O primeiro cenário que vamos testar é se ele vai finalizar corretamente um leilão.
 *  deveriaFinalizarUmLeilao. Aqui precisamos de um atributo, que vai ser a classe que
 *  estamos testando. Então, ‘private FinalizarLeilaoService service’.
 *
 *  Agora no nosso método a primeira coisa que precisamos fazer é instanciar essa
 *  classe service, ‘service = new FinalizarLeilaoService’, porém aqui já vamos ter um problema.
 *  Repare que na classe FinalizarLeilaoService ela tem essa dependência, que é o LeilaoDao, e é
 *  a classe que vamos mockar, porém ela está sendo injetada com @Autowired do Springboot, e essa
 *  injeção de dependências está sendo realizada pelo atributo.
 *
 * Isso é um problema, porque na classe de teste quando instanciar essa classe service vou
 * precisar passar aquele atributo, no caso um mock, para substituir aquele atributo. Só que c
 * omo vou passar esse atributo se ele está sendo injetado diretamente no atributo?
 *
 * Esse mock precisaria ser passado de outra maneira. O ideal, sempre que temos
 * testes automatizados, existe uma boa prática que é a injeção de dependências,
 * sempre deveria ser feita pelo construtor, porque isso já deixa óbvio na classe, o
 * construtor já deixa óbvio que essa classe tem essa, essa e essa dependência e com o
 * construtor consigo passar um mock como parâmetro nos testes. O construtor deixa
 * muito mais simples.
 *
 * Vou substituir aquele @Autowired, ao invés de deixar em cima do atributo, vou deixar no
 * construtor, só preciso gerar esse construtor primeiramente. Vou gerar esse construtor,
 * vou apagar esse super que já vem automaticamente e colocar o @Autowired aqui.
 *
 * Usando o construtor, além de deixar explícito que essa classe depende dessa informação,
 * conseguimos passar um mock como parâmetro nas classes de teste. Fica muito mais fácil de
 * testar com a utilização de um construtor, fica essa dica aí.
 *
 * Agora na nossa classe de teste perceba que começou a dar erro de compilação. Na hora
 * de instanciar nossa classe service preciso passar a dao como parâmetro. Só que aí que está.
 * Não vamos passar uma classe dao de verdade. Vamos passar um mock. Vou pedir para o Mockito
 * criar um mock para nós, e isso já vimos como fazer.
 *
 *  Declaro uma variável local e vou usar aquela classe Mockito.mock, passo como parâmetro
 * ‘LeilaoDao.class’. Dessa maneira o Mockito vai criar o mock da nossa classe LeilaoDao
 * e eu pego esse mock e passo como parâmetro para a classe FinalizarLeilaoService.
 *
 *  Até aqui nenhuma novidade. Já podemos fazer algumas melhorias antes de dar prosseguimento.
 *  Por exemplo, essa é uma das maneiras que vimos de criar um mock com Mockito, chamando
 *  diretamente o método estático mock da classe Mockito. Porém tem outras formas mais
 *  interessantes.
 *
 *  Podemos declarar o mock como sendo um atributo também da nossa classe de teste,
 *  então ‘private LeilaoDao leilaoDao;’, vou excluir essa variável local, preciso renomear
 *  para LeilaoDao. Só que para dizer para o Mockito que esse atributo é um mock, que é para
 *  ele automaticamente criar um mock, usamos uma notação do Mockito @Mock.
 *
 * Colocamos essa notação em cima do atributo na classe de teste, e com isso o Mockito sabe que
 * é para criar um mock dessa classe. Porém ele não faz isso de maneira automática. Precisamos
 * indicar para o Mockito para ele ler todos os atributos da nossa classe de teste, todos que
 * estiverem com @Mock e pedir para ele criar o mock automaticamente.
 *
 * Para fazer isso, ao invés de fazer isso no método, já que vou precisar criar esses mocks em
 * todos os métodos, vamos usar o recurso do JUnit, o @BeforeEach. JUnit, antes de cada um dos
 * métodos de teste, rode esse método aqui, ‘public void beforeEach()’, e aqui já podemos até
 * instanciar nossa service, ‘this.service = new FinalizarLeilaoService(LeilaoDao)’, passando
 * LeilaoDao como parâmetro. Só que antes vamos chamar esse outro método, MockitoAnnotations.initMocks,
 * e passo this como parâmetro.
 *
 Essa classe aqui, o MockitoAnnotations é para ler as anotações do Mockito, o @mock é uma delas,
 e inicializar. De qual classe, de qual objeto? This, da própria classe de teste.
 *
 *  Agora, quando chegar no nosso método de teste ele já inicializou nossos mocks e já criou nossa
 *  service passando LeilaoDao como parâmetro. E aí podemos de fato fazer o teste.
 *
 * Porém, para fazer esse teste, o teste no caso seria chamar aquele método, pegar o
 * service.finalizarLeiloesExpirados. Só que se chamarmos esse método, o que ele faz?
 * Ele pega o leilões, o dao, chama buscarLeiloesExpirados. Só que como estamos passando um
 * mock ele sempre vai devolver uma lista vazia, e não quero que ele devolva uma lista vazia
 * aqui, quero passar determinada lista com leilões e verificar depois se para cada leilão
 * desse se ele finalizou de fato.
 *
 * Voltando para o nosso teste, antes de chamar esse método, preciso criar uma lista
 * com vários leilões que vão ser os leilões de exemplo que vou utilizar no método.
 * Para fazer isso preciso criar alguns leilões. Ao invés de fazer isso dentro do método,
 * vou criar um método privado.
 *
 * Aqui não é nada de mais, é só um método que cria uma lista de leilões em memória.
 * Pega uma lista de leilões, cria um leilão, tem um nome qualquer, um lance inicial
 * de 500, um usuário fulano. Cria dois lances para esse leilão, um lance do usuário
 * beltrano de 600 reais, um lance do usuário ciclano de 900 reais, adiciona esses dois l
 * ances no leilão, adiciona esse leilão na lista e devolve essa lista.
 *
 *  São só as informações que preciso de entrada para o meu teste. Antes de chamar meu método
 *  vou precisar de todos esses parâmetros aqui. Só para adiantar, já criei aqui. Então agora
 *  posso jogar isso numa variável List<Leilao> leiloes =leiloes().
 *
 * Eu quero agora fazer o seguinte. Preciso dizer para o Mockito de alguma maneira,
 * “Mockito, quando o mock, nosso LeilaoDao, quando o método buscarLeiloesExpirados for
 * chamado, não quero que você me devolva uma lista vazia, quero que você me devolva essa lista
 * aqui, porque ela tem alguns objetos que depois quero verificar”.
 *
 * E aí que está. Como faço para ensinar isso para o Mockito, como faço para manipular aquele mock.
 * Falar “Mockito, quando esse mock tiver um método chamado faz isso”, quero manipular esse mock,
 * não quero que ele apenas devolva vazio nos métodos.
 * agora já começamos a escrever nosso teste, aprendemos outra maneira de inicializar nossos mocks, com @mock em cima de cada mock, no caso desse teste só tenho um único mock, e usando a classe ‘MockitoAnnotations.initMocks’. Já podemos dar prosseguimento ao nosso teste.
 *
 *  Tínhamos parado na linha 30. Eu já fiz toda a configuração dos mocks, e tenho esse problema agora
 *  , queria que nesse teste, nesse cenário de teste, quando fôssemos chamar o mock, aquele método
 *  para buscar os leilões expirados, ele não devolvesse uma lista vazia. Eu quero que ele de volta
 *  essa lista, que é uma lista que já configurei, que tem um leilão com alguns lances, para
 *  conseguirmos simular o comportamento.
 *
 *  Então nem sempre quero que o mock apenas finja que chamou o método e não faça nada. Às vezes
 *  quero que quando um determinado método for chamado ele devolva alguma informação específica,
 *  porque isso faz parte do meu teste, e é justamente nosso cenário aqui.
 *
 * Para fazer isso, existe naquela própria classe Mockito um outro método, além daquele método
 * mock para criar um mock, existe um método chamado when, que serve para manipular nosso mock.
 * Para dizer “Mockito, quando, when, esse método do mock for chamado, devolva isso”, então consigo
 * configurar nosso mock para que ele devolva determinadas informações em alguns cenários de teste.
 *
 *  Vamos ver como funciona esse when, ‘Mockito.when(leilaoDao.buscarLeiloesExpirados())’, qual
 *  é o método a ser chamado no mock? É nosso LeilaoDao, preciso passar quem é o mock, e qual o
 *  método que quero simular um comportamento? É o método buscarLeiloesExpirados.
 *
 *  Quando o método buscarLeiloesExpirados for chamado, então retorne e digo para ele o que é
 *  para devolver, passo minha variável leilões. Essa é a maneira de simularmos, manipularmos
 *  o comportamento de determinado método de um mock. O mock não necessariamente o método dele
 *  é engessado, sempre vai fazer uma coisa. Posso configurar para ele devolver outra coisa,
 *  para executar algum comportamento em específico.
 *
 *  Agora já fiz essa configuração. Mockito, quando o LeilaoDao.buscarLeiloesExpirados for
 *  chamado, devolva essa lista. E esse método é chamado no caso quando chamarmos nosso
 *  service finalizarLeiloesExpirados.
 *
 *  A primeira linha do método vai ser essa, vai ser chamar esse nosso método, só que agora
 *  ele não devolve uma lista vazia, ele vai devolver aquela lista que tem um leilão com dois
 *  lances. Então ele vai executar esse loop, vai executar essa lógica, e agora preciso verificar
 *  se a lógica foi executada corretamente.
 *
 *
 *  Agora vamos voltar para o teste. Depois que chamei nosso método de fato que quero testar,
 *  já posso fazer os asserts para verificar se tudo ocorreu conforme o esperado. Essa lista só
 *  tem um único leilão. Vou guardar numa variável ‘Leilao leilao = leiloes.get(0);’.
 *
 *  Agora o que quero fazer com esse leilão? Se esse leilão tiver sido finalizado, lembra
 *  que na nossa classe de serviço ele não chama o método fechar? O que o método fechar faz?
 *  ‘this.fechado = true’. Então consigo verificar se o leilão está fechado, se o atributo
 *  fechado foi modificado para true.
 *
 *  Essa é uma das coisas que quero verificar. Vamos fazer um assert aqui,
 *  ‘Assert.assertTrue(leilao.isFechado ());’, que vai me devolver aquele atributo fechado.
 *  Ele tem que estar valendo true. Se ele finalizou, ele estava valendo false, ele tem que
 *  estar valendo true agora.
 *
 * Outra coisa que quero verificar se formos olhar nossa lógica é o lanceVencedor. O lanceVencedor
 * tem que ser o lance de 900 reais. No caso aqui, do nosso leilão, que criamos, tem dois lances,
 * um de 600 e um de 900, esse é o maior lance. Vou verificar se o lance vencedor é o lance cujo
 * valor é 900.
 *
 *  Outro assert aqui, ‘Assert.assertEquals(new, BigDecimal(“900”), leilao.getLanceVencedor().getValor())’,
 *  vou pegar o valor do lance vencedor e tem que ser 900, porque no meu cenário criei um leilão único
 *  que tem dois lances e o maior é o de 900.
 *
 * Duas verificações, e agora olha só também uma outra coisa importante. Perceba que tem essa terceira
 * linha na nossa lógica, ‘leiloes.salvar’, essa linha é importante. Se eu não colocar essa linha ele
 * só fez essa modificação em memória, mas não foi gravada no banco de dados.
 *
 *  Posso verificar se essa linha também está sendo disparada, se o método salvar foi executado.
 *  Posso garantir que minha classe está atualizando isso no banco de dados. Essa modificação foi
 *  executada no banco de dados. No caso, agora quero fazer outra coisa. Quero fazer uma assertiva,
 *  mas em cima do mock.
 *
 *  Nosso LeilaoDao é um mock. O Mockito também tem um esquema pronto para você verificar se um
 *  determinado método de um mock foi executado. É só você chamar outro método, o ‘Mockito.verify’,
 *  existe um método verify, você passa quem é o mock, LeilaoDao, qual o método que você quer
 *  verificar se foi chamado, o método salvar. Passando qual parâmetro? O meu objeto leilão.
 *
 * Aqui, internamente, esse método verify já vai fazer um assert para nós. Essas são as assertivas
 * do nosso teste. Verificar se o leilão foi marcado como fechado, verificar se o lance vencedor
 * é o lance de 900, e verificar se o dao foi chamado, o método chamar passando nosso leilão.
 *
 *  Isso é o que garante que nosso código está funcionando conforme o esperado. Ele já está f
 *  inalizado, vamos rodar esse teste e ver o que vai acontecer, run as JUnit. Vamos executar.
 *
 * Falhou. Deu uma exception unsupportedOperationException. Essa exception aconteceu quando
 * chamamos o método sort, quando chamamos o maior lance dado no leilão. Essa linha 34. Por
 * que aconteceu isso?
 *
 *  Eu peguei a lista de lances, ‘leilao.getLances’, chamei o método sort. Porém ele disse
 *  que não é suportado o método sort. Poxa, mas o que essa lista está me devolvendo. Vamos
 *  dar uma olhada na classe leilão. Quando chamamos o método getLances ele devolve um
 *  collections.unmodifiableList, porque ele não quer deixar que de fora da classe a lista
 *  de lances seja modificada. Então ele devolve uma lista não modificada.
 *
 *  E aí se eu pegar essa lista não modificável e chamar o método sort dá exception. Na
 *  nossa lógica vou ter que alterar esse código, não posso ter uma lista unmodifiable.
 *  Vou criar um new ArrayList passando como parâmetro para o ArrayList os elementos do
 *  leilao.getLances, porque o ArrayList consigo chamar o método sort.
 *
 *  Aqui foi um ajuste necessário no teste. Vamos rodar o teste novamente e vamos ver se a
 *  gora vai passar. Passou com sucesso. Nosso código está funcionando corretamente. Vamos
 *  simular se daria erro se alguém chegasse aqui e comentasse essa linha, apagasse essa
 *  linha. Estou pegando os leilões, marcando o lance vencedor, fechando, mas não estou
 *  salvando no banco de dados. Deveria falhar nosso teste.
 *
 *  Vamos rodar. O teste falhou. Olha que legal a mensagem que ele dá do Mockito. Você queria
 *  que o método fosse chamado, mas ele não foi invocado. O método salvar. Ou seja, o método
 *  salvar não foi chamado nessa linha. Se alguém por acaso esqueceu de chamar o dao para
 *  atualizar no banco de dados vai dar erro.
 *
 * E se eu comentar essa linha que marca o leilão como fechado? Deveria falhar também dizendo
 * que o leilão não mais está fechado, mas sim aberto. Vamos rodar o teste. Falhou agora outro
 * assert, o ‘assertTrue(leilao.isFechado())’, então leilão não está mais fechado.
 *
 *  Percebe? E se eu não marcar o lance vencedor, será que também vai falhar? Vamos rodar o teste
 *  e ver o que vai acontecer. Deu nullpointer exception, porque o getLanceVencedor está vindo nulo,
 *  não tem mais nenhum lance, já que não setei o lance vencedor, ele está nulo.
 *
 * Percebe que o teste está garantindo todos os comportamentos esperados? Então o teste está
 * me garantindo que o leilão, quando eu for chamar o finalizarLeiloesExpirados, ele vai pegar
 * o leilão, vai marcar ele como fechado, vai dizer quem foi o lance vencedor e vai verificar
 * se ele foi atualizado no banco de dados.
 *
 *  Percebam que temos um teste aqui que consegue verificar esses comportamentos? Só que sem ir
 *  no banco de dados de verdade, só simulando que foi no banco de dados. Para o meu teste quero testar a lógica, não quero saber se está indo no banco de dados ou não. Independente do banco de dados quero verificar se a lógica está se comportando conforme o esperado. Esse é o uso ideal de um teste utilizando mocks.
 *
 *  Finalizamos nosso teste aqui, está tudo funcionando. Vou só rodar de novo, descomentei tudo.
 *  Deveria voltar a funcionar. E o teste roda rapidamente, porque não é um teste de integração,
 *  continua sendo um teste de unidade. Na próxima aula continuamos pensando em outros testes,
 *  vejo vocês lá.
 */
