package com.ufcg.psoft.mercadofacil.controller;

import com.ufcg.psoft.mercadofacil.dto.EstabelecimentoPostPutRequestDTO;
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

    @PatchMapping("/aceitar_entregador/{associacaoId}")
    public ResponseEntity<?> associarEntregador(@PathVariable Long associacaoId,
                                                @RequestParam(value = "codigoDeAcesso") String codigoDeAcesso){

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(associacaoService.aceitarAssociacao(associacaoId, codigoDeAcesso));
    }

    @DeleteMapping("/rejeitar_entregador/{associacaoId}")
    public ResponseEntity<?> rejeitarEntregador(@PathVariable Long associacaoId,
                                                @RequestParam(value = "codigoDeAcesso") String codigoDeAcesso) {
        // ----> Verificar se o código de acesso está correto <----
        associacaoService.recusarAssociacao(associacaoId, codigoDeAcesso);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body("");
    }


}
