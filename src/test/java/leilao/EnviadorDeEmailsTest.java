package leilao;

import br.com.alura.leilao.model.Lance;
import br.com.alura.leilao.model.Leilao;
import br.com.alura.leilao.model.Pagamento;
import br.com.alura.leilao.model.Usuario;
import br.com.alura.leilao.service.EnviadorDeEmails;
import br.com.alura.leilao.service.GeradorDePagamento;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
//NAO CONSIDERAR PARA ESTUDOS
public class EnviadorDeEmailsTest {

    @Mock
    EnviadorDeEmails enviadorDeEmails;

    @Captor // sempre que quisermos capturar um objeto que foi passado no metodo de um mock usamos um caption
    private ArgumentCaptor<Pagamento> captor;

    @BeforeEach
    public void beforeEach() {
        MockitoAnnotations.initMocks(this);//para ler anotacoes do mockito e inicializar
        this.enviadorDeEmails = new EnviadorDeEmails();
    }

    @Test
    void deveriaEnviarUmEmail(){

        Leilao leilao=leilao();
        Lance lanceVencedor = leilao.getLanceVencedor();

        Assert.assertNull(enviadorDeEmails.enviarEmailVencedorLeilao(lanceVencedor));



    }
    private Leilao leilao() {

        Leilao leilao = new Leilao("Celular", new BigDecimal("500"), new Usuario("Fulano"));

        Lance lance = new Lance(new Usuario("Ciclano"), new BigDecimal("900"));

        leilao.propoe(lance);

        leilao.setLanceVencedor(lance);

        return leilao;

    }
}
