package com.ufcg.psoft.mercadofacil.controller;

import com.ufcg.psoft.mercadofacil.dto.EntregadorGetResponseDTO;
import com.ufcg.psoft.mercadofacil.dto.EntregadorPostPutRequestDTO;
import com.ufcg.psoft.mercadofacil.model.Entregador;
import com.ufcg.psoft.mercadofacil.service.associacao.AssociacaoService;
import com.ufcg.psoft.mercadofacil.service.entregador.EntregadorAlterarService;
import com.ufcg.psoft.mercadofacil.service.entregador.EntregadorCriarService;
import com.ufcg.psoft.mercadofacil.service.entregador.EntregadorExcluirService;
import com.ufcg.psoft.mercadofacil.service.entregador.EntregadorListarService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(
        value = "/v1/entregadores",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class EntregadorV1Controller {

    @Autowired
    EntregadorAlterarService entregadorAlterarService;

    @Autowired
    EntregadorListarService entregadorListarService;

    @Autowired
    EntregadorCriarService entregadorCriarService;

    @Autowired
    EntregadorExcluirService entregadorExcluirService;

    @Autowired
    AssociacaoService associacaoService;


    @GetMapping("/{id}")
    public ResponseEntity<EntregadorGetResponseDTO> buscarUmEntregador(
            @PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(entregadorListarService.listar(id).get(0));
    }

    @GetMapping("")
    public ResponseEntity<List<EntregadorGetResponseDTO>> buscarTodosEntregadores() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(entregadorListarService.listar(null));
    }

    @PostMapping()
    public ResponseEntity<Entregador> criarEntregador(
            @RequestBody @Valid EntregadorPostPutRequestDTO entregadorPostPutRequestDTO
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(entregadorCriarService.criar(entregadorPostPutRequestDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Entregador> atualizarEntregador(
            @PathVariable Long id,
            @RequestParam(value = "codigoDeAcesso") String codigoDeAcesso,
            @RequestBody @Valid EntregadorPostPutRequestDTO entregadorPostPutRequestDTO
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(entregadorAlterarService.alterar(id, codigoDeAcesso, entregadorPostPutRequestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluirEntregador(
            @PathVariable Long id,
            @RequestParam(value = "codigoDeAcesso") String codigoDeAcesso
    ) {
        entregadorExcluirService.excluir(id, codigoDeAcesso);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body("");
    }

    @PostMapping("/solicitar-associacao/{entregadorId}/{idEstabelecimento}")
    public ResponseEntity<?> solicitarAssociacao(@RequestParam String codigoAcessoEntregador,
                                                    @PathVariable Long idEstabelecimento,
                                                    @PathVariable Long entregadorId) {
        // Lógica para solicitar associação a um estabelecimento

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(associacaoService.associarEntregadorEstabelecimento(entregadorId, idEstabelecimento, codigoAcessoEntregador));
    }



}
