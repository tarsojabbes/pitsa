package com.ufcg.psoft.mercadofacil.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.MediaType;

import com.ufcg.psoft.mercadofacil.model.Sabor;
import com.ufcg.psoft.mercadofacil.service.cardapio.CardapioService;

import java.util.List;

@RestController
@RequestMapping(
    value = "/v1/cardapios",
    produces = MediaType.APPLICATION_JSON_VALUE
)
public class CardapioV1Controller {
    
    @Autowired
    CardapioService cardapioService;

    @GetMapping("/{estabelecimentoId}/completo")
    public ResponseEntity<List<Sabor>> cardapioCompleto(
        @PathVariable Long estabelecimentoId
    ){

        return ResponseEntity.status(HttpStatus.OK).body(cardapioService.cardapioCompleto(estabelecimentoId));

    }

    @GetMapping("/{estabelecimentoId}/salgados")
    public ResponseEntity<List<Sabor>> cardapioSalgados(
        @PathVariable Long estabelecimentoId
    ){

        return ResponseEntity.status(HttpStatus.OK).body(cardapioService.cardapioSaboresSalgados(estabelecimentoId));
        
    }

    @GetMapping("/{estabelecimentoId}/doces")
    public ResponseEntity<List<Sabor>> cardapioDoces(
        @PathVariable Long estabelecimentoId
    ){
        return ResponseEntity.status(HttpStatus.OK).body(cardapioService.cardapioSaboresDoces(estabelecimentoId));
    }

}
