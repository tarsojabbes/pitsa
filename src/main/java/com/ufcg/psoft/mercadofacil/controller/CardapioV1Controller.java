package com.ufcg.psoft.mercadofacil.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.MediaType;

import com.ufcg.psoft.mercadofacil.model.Sabor;
import com.ufcg.psoft.mercadofacil.service.cardapio.CardapioService;

import java.util.List;

@RestController
@RequestMapping(
    value = "/v1/cardapio",
    produces = MediaType.APPLICATION_JSON_VALUE
)
public class CardapioV1Controller {
    
    @Autowired
    CardapioService cardapioService;

    @GetMapping("/completo")
    public ResponseEntity<List<Sabor>> cardapioCompleto(){

        return ResponseEntity.status(HttpStatus.OK).body(cardapioService.cardapioCompleto());

    }

    @GetMapping("/salgados")
    public ResponseEntity<List<Sabor>> cardapioSalgados(){

        return ResponseEntity.status(HttpStatus.OK).body(cardapioService.cardapioSaboresSalgados());
        
    }

    @GetMapping("/doces")
    public ResponseEntity<List<Sabor>> cardapioDoces(){

        return ResponseEntity.status(HttpStatus.OK).body(cardapioService.cardapioSaboresDoces());

    }

}
