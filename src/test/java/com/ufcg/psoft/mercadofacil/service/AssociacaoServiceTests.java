package com.ufcg.psoft.mercadofacil.service;

import com.ufcg.psoft.mercadofacil.exception.CodigoDeAcessoInvalidoException;
import com.ufcg.psoft.mercadofacil.exception.EntregadorNaoAutorizadoException;
import com.ufcg.psoft.mercadofacil.exception.EntregadorNaoExisteException;
import com.ufcg.psoft.mercadofacil.exception.EstabelecimentoNaoExisteException;
import com.ufcg.psoft.mercadofacil.model.Associacao;
import com.ufcg.psoft.mercadofacil.model.Entregador;
import com.ufcg.psoft.mercadofacil.model.Estabelecimento;
import com.ufcg.psoft.mercadofacil.repository.AssociacaoRepository;
import com.ufcg.psoft.mercadofacil.repository.EntregadorRepository;
import com.ufcg.psoft.mercadofacil.repository.EstabelecimentoRepository;
import com.ufcg.psoft.mercadofacil.service.associacao.AssociacaoService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static com.ufcg.psoft.mercadofacil.model.DisponibilidadeEntregador.ATIVO;
import static com.ufcg.psoft.mercadofacil.model.DisponibilidadeEntregador.DESCANSO;
import static com.ufcg.psoft.mercadofacil.model.TipoDoVeiculo.MOTO;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AssociacaoServiceTests {

    @Autowired
    AssociacaoService associacaoService;

    @Autowired
    EntregadorRepository entregadorRepository;

    @Autowired
    EstabelecimentoRepository estabelecimentoRepository;

    @Autowired
    AssociacaoRepository associacaoRepository;

    Entregador entregador;
    Estabelecimento estabelecimento;

    @BeforeEach
    public void setUp() {
        entregador = entregadorRepository.save(Entregador.builder()
                .codigoDeAcesso("123456")
                .nome("Steve Jobs")
                .corDoVeiculo("preto")
                .placaDoVeiculo("ABC1234")
                .tipoDoVeiculo(MOTO)
                .build());

        estabelecimento = estabelecimentoRepository.save(Estabelecimento.builder()
                .codigoDeAcesso("1234567")
                .nome("Estabelecimento A")
                .build());
    }

    @AfterEach
    public void tearDown() {
        associacaoRepository.deleteAll();
        entregadorRepository.deleteAll();
        estabelecimentoRepository.deleteAll();

    }

    @Test
    @Transactional
    @DisplayName("Quando crio uma associação")
    public void quandoCrioUmaAssociacao() {
        Long entregadorId = entregador.getId();
        Long estabelecimentoId = estabelecimento.getId();
        Associacao associacao = associacaoService.associarEntregadorEstabelecimento(entregadorId, estabelecimentoId, "123456");
        assertNotNull(associacao);
        assertEquals(entregador.getId(), associacao.getEntregador().getId());
        assertEquals(estabelecimento.getId(), associacao.getEstabelecimento().getId());
    }

    @Test
    @Transactional
    @DisplayName("Quando busco uma associação válida no banco de dados")
    public void quandoBuscoUmaAssociacaoValida() {
        Long entregadorId = entregador.getId();
        Long estabelecimentoId = estabelecimento.getId();
        associacaoService.associarEntregadorEstabelecimento(entregadorId, estabelecimentoId, "123456");

        Associacao associacao = associacaoService.buscarAssociacao(entregadorId, estabelecimentoId, "1234567");
        assertNotNull(associacao);
        assertEquals(entregador.getId(), associacao.getEntregador().getId());
        assertEquals(estabelecimento.getId(), associacao.getEstabelecimento().getId());
    }

    @Test
    @Transactional
    @DisplayName("Quando busco uma associação inválida no banco de dados")
    public void quandoBuscoUmaAssociacaoInvalida() {
        Long entregadorId = entregador.getId();
        Long estabelecimentoId = estabelecimento.getId();

        Associacao associacao = associacaoService.buscarAssociacao(entregadorId, estabelecimentoId, "1234567");
        assertNull(associacao);
    }

    @Test
    @Transactional
    @DisplayName("Quando passo um Id de estabelecimento invalido")
    public void quandoPassoIdDeEstabelecimentoInvalido() {
        Long entregadorId = entregador.getId();
        assertThrows(EstabelecimentoNaoExisteException.class, () -> {
            associacaoService.associarEntregadorEstabelecimento(entregadorId, 2L, "123456");
        });
    }

    @Test
    @Transactional
    @DisplayName("Quando passo um Id de entregador invalido")
    public void quandoPassoIdDeEntregadorInvalido() {
        Long estabelecimentoId = estabelecimento.getId();
        assertThrows(EntregadorNaoExisteException.class, () -> {
            associacaoService.associarEntregadorEstabelecimento(2L, estabelecimentoId, "123456");
        });
    }

    @Test
    @Transactional
    @DisplayName("Quando aceito uma solicitação de associação")
    public void quandoAceitoAssociacao() {
        Long entregadorId = entregador.getId();
        Long estabelecimentoId = estabelecimento.getId();
        Associacao associacao = associacaoService.associarEntregadorEstabelecimento(entregadorId, estabelecimentoId, "123456");

        associacaoService.aceitarAssociacao(associacao.getId(), estabelecimento.getCodigoDeAcesso());
        // A associação continua no banco de dados
        Optional<Associacao> optionalAssociacao = associacaoRepository.findById(associacao.getId());
        assertTrue(optionalAssociacao.isPresent());
    }

    @Test
    @Transactional
    @DisplayName("Quando recuso uma solicitação de associação")
    public void quandoRecusoUmaAssociacao() {
        Long entregadorId = entregador.getId();
        Long estabelecimentoId = estabelecimento.getId();
        Associacao associacao = associacaoService.associarEntregadorEstabelecimento(entregadorId, estabelecimentoId, "123456");

        associacaoService.recusarAssociacao(associacao.getId(), estabelecimento.getCodigoDeAcesso());
        // A associação não continua no banco de dados
        Optional<Associacao> optionalAssociacao = associacaoRepository.findById(associacao.getId());
        assertFalse(optionalAssociacao.isPresent());
    }

    @Test
    @Transactional
    @DisplayName("Quando altero a disponibilidade do entregador de uma associação para ativo")
    public void quandoAlteroDisponibilidadeAtivo() {
        Associacao associacao = associacaoRepository.save(Associacao.builder()
                .estabelecimento(estabelecimento)
                .entregador(entregador)
                .statusAssociacao(true)
                .build());

        associacaoService.alterarDisponibilidadeEntregador(entregador.getId(), ATIVO, associacao.getId(), entregador.getCodigoDeAcesso());

        assertEquals(ATIVO, associacao.getDisponibilidadeEntregador());
    }

    @Test
    @Transactional
    @DisplayName("Quando altero a disponibilidade do entregador de uma associação para descanso")
    public void quandoAlteroDisponibilidadeDescanso() {
        Associacao associacao = associacaoRepository.save(Associacao.builder()
                .estabelecimento(estabelecimento)
                .entregador(entregador)
                .statusAssociacao(true)
                .build());

        associacaoService.alterarDisponibilidadeEntregador(entregador.getId(), DESCANSO, associacao.getId(), entregador.getCodigoDeAcesso());

        assertEquals(DESCANSO, associacao.getDisponibilidadeEntregador());
    }

    @Test
    @Transactional
    @DisplayName("Quando altero a disponibilidade do entregador de uma associação passando o ID de outro entregador")
    public void quandoAlteroDisponibilidadeDeOutroEntregador() {

        Entregador entregador2 = entregadorRepository.save(Entregador.builder()
                .codigoDeAcesso("123456")
                .nome("Joao Steven")
                .corDoVeiculo("preto")
                .placaDoVeiculo("ABC1234")
                .tipoDoVeiculo(MOTO)
                .build());

        Associacao associacao = associacaoRepository.save(Associacao.builder()
                .estabelecimento(estabelecimento)
                .entregador(entregador)
                .statusAssociacao(true)
                .build());

        assertThrows(EntregadorNaoAutorizadoException.class, () -> {
            associacaoService.alterarDisponibilidadeEntregador(entregador2.getId(), DESCANSO, associacao.getId(), entregador.getCodigoDeAcesso());
        });

        assertNull(associacao.getDisponibilidadeEntregador());
    }

    @Test
    @Transactional
    @DisplayName("Quando altero a disponibilidade do entregador de uma associação passando codigo de acesso invalido")
    public void quandoAlteroDisponibilidadeCodigoAcessoInvalido() {

        Associacao associacao = associacaoRepository.save(Associacao.builder()
                .estabelecimento(estabelecimento)
                .entregador(entregador)
                .statusAssociacao(true)
                .build());

        assertThrows(CodigoDeAcessoInvalidoException.class, () -> {
            associacaoService.alterarDisponibilidadeEntregador(entregador.getId(), DESCANSO, associacao.getId(), "12345");
        });

        assertNull(associacao.getDisponibilidadeEntregador());
    }

}
