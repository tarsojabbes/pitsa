package com.ufcg.psoft.mercadofacil.controller;

import com.electronwill.nightconfig.core.conversion.Path;
import com.ufcg.psoft.mercadofacil.dto.SaborAlterarDisponivelDTO;
import com.ufcg.psoft.mercadofacil.dto.SaborPostPutRequestDTO;
import com.ufcg.psoft.mercadofacil.model.Sabor;
import com.ufcg.psoft.mercadofacil.service.sabor.*;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(
        value = "/v1/sabores",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class SaborV1Controller {

    @Autowired
    SaborListarService saborListarService;

    @Autowired
    SaborCriarService saborCriarService;

    @Autowired
    SaborAlterarService saborAlterarService;

    @Autowired
    SaborExcluirService saborExcluirService;

    @Autowired
    SaborAlterarDisponivelService saborAlterarDisponivelService;

    @GetMapping("/{id}")
    public ResponseEntity<Sabor> buscarSabor(@PathVariable Long id) {

        return ResponseEntity.status(HttpStatus.OK).body(saborListarService.listar(id, null).get(0));
        
    }

    @GetMapping("")
    public ResponseEntity<List<Sabor>> buscarTodosSabores() {

        return ResponseEntity.status(HttpStatus.OK).body(saborListarService.listar(null, null));

    }

    @GetMapping("/estabelecimento/{estabelecimentoId}")
    public ResponseEntity<List<Sabor>> buscarSaborPorEstabelecimentoID(@PathVariable Long estabelecimentoId) {
        return ResponseEntity.status(HttpStatus.OK).body(saborListarService.listar(null, estabelecimentoId));
    }

    @PostMapping()
    public ResponseEntity<Sabor> criarSabor(@RequestBody @Valid SaborPostPutRequestDTO saborPostPutRequestDTO,
                @RequestParam(value = "codigoDeAcesso", required = true) String codigoDeAcesso) {

        return ResponseEntity.status(HttpStatus.OK).body(saborCriarService.criar(codigoDeAcesso, saborPostPutRequestDTO));

    }

    @PutMapping("/{id}")
    public ResponseEntity<Sabor> atualizarSabor(@PathVariable Long id, 
    @RequestBody @Valid SaborPostPutRequestDTO saborPostPutRequestDTO,
                @RequestParam(value = "codigoDeAcesso", required = true) String codigoDeAcesso) {

        return ResponseEntity.status(HttpStatus.OK).body(saborAlterarService.alterar(id, codigoDeAcesso, saborPostPutRequestDTO));

    }

    @DeleteMapping("/{id}")
    public void excluirSabor(@PathVariable Long id,
    @RequestParam(value = "codigoDeAcesso", required = true) String codigoDeAcesso) {
        saborExcluirService.excluir(id, codigoDeAcesso);
    }

    @PatchMapping("/{id}/disponibilidade")
    public ResponseEntity<Sabor> atualizarDisponibilidade(
            @PathVariable Long id,
            @RequestBody @Valid SaborAlterarDisponivelDTO saborAlterarDisponivelDTO,
            @RequestParam(value = "codigoDeAcesso", required = true) String codigoDeAcesso
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(saborAlterarDisponivelService.alterar(id, codigoDeAcesso, saborAlterarDisponivelDTO));
    }

    @GetMapping("/{id}/disponibilidade")
    public ResponseEntity<Boolean> getDisponibilidadeDeSabor(
            @PathVariable Long id
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(saborListarService.listar(id, null).get(0).getDisponivel());
    }
}