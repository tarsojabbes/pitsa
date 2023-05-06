package com.ufcg.psoft.mercadofacil.controller;

import com.ufcg.psoft.mercadofacil.dto.SaborPostPutRequestDTO;
import com.ufcg.psoft.mercadofacil.model.Sabor;
import com.ufcg.psoft.mercadofacil.service.sabor.SaborCriarService;
import com.ufcg.psoft.mercadofacil.service.sabor.SaborListarService;
import com.ufcg.psoft.mercadofacil.service.sabor.SaborExcluirService;
import com.ufcg.psoft.mercadofacil.service.sabor.SaborAlterarService;

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

    @GetMapping("/{id}")
    public ResponseEntity<Sabor> buscarSabor(@PathVariable Long id) {

        return ResponseEntity.status(HttpStatus.OK).body(saborListarService.listar(id).get(0));
        
    }

    @GetMapping("")
    public ResponseEntity<List<Sabor>> buscarTodosSabores() {

        return ResponseEntity.status(HttpStatus.OK).body(saborListarService.listar(null));

    }

    @PostMapping()
    public ResponseEntity<Sabor> criarSabor(@RequestBody @Valid SaborPostPutRequestDTO saborPostPutRequestDTO) {

        return ResponseEntity.status(HttpStatus.OK).body(saborCriarService.criar(saborPostPutRequestDTO));

    }

    @PutMapping("/{id}")
    public ResponseEntity<Sabor> atualizarSabor(@PathVariable Long id, @RequestBody @Valid SaborPostPutRequestDTO saborPostPutRequestDTO) {

        return ResponseEntity.status(HttpStatus.OK).body(saborAlterarService.alterar(id, saborPostPutRequestDTO));

    }

    @DeleteMapping("/{id}")
    public void excluirSabor(@PathVariable Long id) {

        saborExcluirService.excluir(id);

    }
}