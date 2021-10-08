package leilao;

import br.com.alura.leilao.dao.PagamentoDao;
import br.com.alura.leilao.model.Lance;
import br.com.alura.leilao.model.Leilao;
import br.com.alura.leilao.model.Pagamento;
import br.com.alura.leilao.model.Usuario;
import br.com.alura.leilao.service.FinalizarLeilaoService;
import br.com.alura.leilao.service.GeradorDePagamento;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * O ideal é evitarmos ao máximo métodos estáticos, criar objetos via métodos estáticos dentro
 * dessas classes, o ideal é sempre criar abstrações para substituir isso. No caso, nem precisamos
 * criar uma abstração, porque ela já existe. Como LocalDate é do próprio Java, vem lá do pacote
 * Java time, API de datas do Java 8, já temos uma abstração que se chama clock. Tem uma classe
 * chamada clock no Java.
 */
// Argument Captor nos ajuda a capturar um objeto criado internamente na classe sendo testada, quando ele é passado como parâmetro para um método do mock.
public class GeradorDePagamentoTest {

    @Mock
    private GeradorDePagamento geradorDePagamento;

    @Mock
    private PagamentoDao pagamentoDao;

    @Captor // sempre que quisermos capturar um objeto que foi passado no metodo de um mock usamos um caption
    private ArgumentCaptor<Pagamento>captor;

    @Mock
    private Clock clock;

    @BeforeEach
    public void beforeEach() {
        MockitoAnnotations.initMocks(this);//para ler anotacoes do mockito e inicializar
        this.geradorDePagamento = new GeradorDePagamento(pagamentoDao,clock);
    }
    //Tratamento de metodos estaticos
    @Test
    void deveriaCriarPagamentoParaVencendorDoLeilao(){

        Leilao leilao = leilao();
        Lance vencedor = leilao.getLanceVencedor();

        LocalDate data = LocalDate.of(2020,12,07);

        Instant instant = data.atStartOfDay(ZoneId.systemDefault()).toInstant();

        Mockito.when(clock.instant()).thenReturn(instant);
        Mockito.when(clock.getZone()).thenReturn(ZoneId.systemDefault());

        geradorDePagamento.gerarPagamento(vencedor);


        Mockito.verify(pagamentoDao).salvar(captor.capture());

        Pagamento pagamento =captor.getValue(); // captor.getValue()-acessa as informacoes capturadas na linha anterior.

        assertEquals(data.plusDays(1), pagamento.getVencimento());//LocalDate.now().plusDays(1)- data de vencimento tem que ser amanha,
        assertEquals(vencedor.getValor(),pagamento.getValor());
        Assert.assertFalse(pagamento.getPago());
        assertEquals(vencedor.getLeilao(),pagamento.getLeilao());
        assertEquals(vencedor.getUsuario(),pagamento.getUsuario());
        assertEquals(leilao,pagamento.getLeilao());
    }
    private Leilao leilao() {

        Leilao leilao = new Leilao("Celular", new BigDecimal("500"), new Usuario("Fulano"));

        Lance lance = new Lance(new Usuario("Ciclano"), new BigDecimal("900"));

        leilao.propoe(lance);

        leilao.setLanceVencedor(lance);

        return leilao;

    }
}
/**
 *  Mockito.verify(pagamentoDao).salvar(captor.capture())-Aqui estou falando “Mockito, verifica para mim
 *  se o PagamentoDao teve o método salvar chamado e já de cara captura esse parâmetro
 *  que foi passado’, ele é um objeto que foi criado lá dentro, só que consigo capturá-lo com o Mockito,
 *  e aí ele vai capturar esse objeto para mim.
 *  Existe um conceito no Mockito chamado capture, é para capturar determinado objeto. É isso que vamos
 * precisar utilizar aqui, pedir para o Mockito capturar esse objeto que foi criado e devolver para nós
 * no teste para fazer as assertivas e verificar se ele foi criado da maneira correta
 *
 *
 *
 */