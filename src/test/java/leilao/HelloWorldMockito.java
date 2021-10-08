package leilao;

import br.com.alura.leilao.dao.LeilaoDao;
import br.com.alura.leilao.model.Leilao;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

public class HelloWorldMockito {
    /**
     * Mockito.mock - Utiliza recursos como reflection, dentre outras coisas, para criar essa classe em memória durante
     * a execução da nossa aplicação. Não importam muito os detalhes técnicos de como ele faz isso, mas é
     * mais ou menos com aquele exemplo que tinha mostrado no vídeo anterior, ele cria uma classe e tem os
     * mesmos métodos da classe original, só que quando eu chamar esses métodos ele vai só simular, fingir
     * que foram chamados de fato.
     */
    @Test
    void hello(){
        LeilaoDao mock  = Mockito.mock(LeilaoDao.class);
        List<Leilao> todos=mock.buscarTodos();
        Assert.assertTrue(todos.isEmpty());
    }
    /**
     * Aqui posso utilizar essa variável mock.buscarTodos, esse é um método que tem na nossa
     * classe LeilaoDao, se abrirmos a classe LeilaoDao e dermos uma olhada, percebam, tem um
     * método buscarTodos. O que esse método faz na classe LeilaoDao original, na verdadeira?
     * Ele pega um entity manager, cria uma query fazendo um select, chama o getResultList.
     *
     * Ou seja, ele vai fazer um select no banco de dados para carregar todos os leilões.
     * Porém no teste, como não é um LeilaoDao de verdade que estamos utilizando, é um mock,
     * quando chamarmos o buscarTodos ele não vai fazer nada, ele vai fingir que foi lá, que
     * chamou o método buscarTodos, e esse método buscarTodos, percebe que ele devolve um
     * list de leilão.Então, o que o Mockito faz? Ele cria um list de leilão vazio e simplesmente
     * devolve esse list para nós. Só que ele não foi no banco de dados de verdade. Ele simplesmente
     * fingiu que executou o método buscarTodos e devolveu uma lista vazia, só. Simulando o comportamento.
     * Posso até guardar essa lista em uma variável em memória. Vou chamar de todos. E só para garantir que
     * essa lista está vazia, que ele não foi no banco de dados de verdade, podemos fazer um assert aqui,
     * ‘Assert.assertTrue(todos.isEmpty ())’, se é vazia essa lista, e está aí um exemplo de teste utilizando
     * mock.
     */
}
