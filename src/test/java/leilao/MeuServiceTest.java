package leilao;

import br.com.alura.leilao.dao.LanceDao;
import br.com.alura.leilao.service.FinalizarLeilaoService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;


/**
 * Injeção sem ter que criar os construtores
 *
 * Você pode utilizar a anotação @InjectMocks do Mockito, que ele vai tentar fazer a injeção de
 * dependências dos mocks via atributos ou métodos setters, além do construtor.
 */
public class MeuServiceTest {

    @Test
    public void x (){

    }
    @Mock
    private LanceDao lanceDao;

    @InjectMocks
    private FinalizarLeilaoService service;
}
