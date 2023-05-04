package com.ufcg.psoft.mercadofacil.controller;

import com.ufcg.psoft.mercadofacil.dto.EstabelecimentoPostPutRequestDTO;
import com.ufcg.psoft.mercadofacil.model.Associacao;
import com.ufcg.psoft.mercadofacil.model.Estabelecimento;
import com.ufcg.psoft.mercadofacil.service.associacao.AssociacaoService;
import com.ufcg.psoft.mercadofacil.service.estabelecimento.EstabelecimentoCriarService;
import com.ufcg.psoft.mercadofacil.service.estabelecimento.EstabelecimentoListarService;
import com.ufcg.psoft.mercadofacil.service.estabelecimento.EstabelecimentoExcluirService;
import com.ufcg.psoft.mercadofacil.service.estabelecimento.EstabelecimentoAlterarService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(
        value = "/v1/estabelecimentos",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class EstabelecimentoV1Controller {
    @Autowired
    EstabelecimentoListarService estabelecimentoListarService;

    @Autowired
    EstabelecimentoCriarService estabelecimentoCriarService;

    @Autowired
    EstabelecimentoAlterarService estabelecimentoAlterarService;

    @Autowired
    EstabelecimentoExcluirService estabelecimentoExcluirService;

    @Autowired
    AssociacaoService associacaoService;

    @GetMapping("/{id}")
    public ResponseEntity<Estabelecimento> buscarEstabelecimento(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(estabelecimentoListarService.listar(id).get(0));
    }

    @GetMapping("")
    public ResponseEntity<List<Estabelecimento>> buscarTodosEstabelecimentos() {
        return ResponseEntity.status(HttpStatus.OK).body(estabelecimentoListarService.listar(null));
    }

    @PostMapping()
    public ResponseEntity<Estabelecimento> criarEstabelecimento(
            @RequestBody @Valid EstabelecimentoPostPutRequestDTO estabelecimentoPostPutRequestDTO
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(estabelecimentoCriarService.criar(estabelecimentoPostPutRequestDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Estabelecimento> atualizarEstabelecimento(
            @PathVariable Long id,
            @RequestBody @Valid EstabelecimentoPostPutRequestDTO estabelecimentoPostPutRequestDTO
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(estabelecimentoAlterarService.alterar(id, estabelecimentoPostPutRequestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluirEstabelecimento(
            @PathVariable Long id
    ) {
        estabelecimentoExcluirService.excluir(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("");
    }


    // ---- Miguel:
    // Acho que deveria dividir essa requisição em 3:
    // Um post, para criar a associação, mas isso no controller de Entregador (pois cria a associaçção).
    //      Receberia o id do Entregador e o id do Estabelecimento
    // Um patch (talvez) para aceitar o entregador ( pois altera uma associação)
    //      Talvez chamar de aceitar associação
    //      Receberia apenas o id da associação
    // um delete para recusar o entragor (pois excluí a associação)
    //      Receberia apenas o id da associação
    @PostMapping("/associar_entregador")
    public ResponseEntity<Void> associarEntregador(@PathVariable Long entregadorId,
                                                   @PathVariable Long estabelecimentoId,
                                                   @PathVariable String codigoAcessoEstabelecimento,
                                                   @RequestBody boolean status
    ) {
        // Lógica para associar um entregador a um estabelecimento
        Associacao associacao = associacaoService.buscarAssociacao(entregadorId, estabelecimentoId, codigoAcessoEstabelecimento);
        if (status){
            associacaoService.aceitarAssociacao(associacao.getId());
        } else{
            associacaoService.recusarAssociacao(associacao.getId());
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
