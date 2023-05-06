package com.ufcg.psoft.mercadofacil.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import com.ufcg.psoft.mercadofacil.model.Sabor;
import com.ufcg.psoft.mercadofacil.service.cardapio.CardapioService;

import java.util.List;

@Controller
public class CardapioV1Controller {
    
    @Autowired
    CardapioService cardapioService;

    public ResponseEntity<List<Sabor>> cardapioCompleto(){

        return ResponseEntity.status(HttpStatus.OK).body(cardapioService.cardapioCompleto());

    }

    public ResponseEntity<List<Sabor>> cardapioSalgados(){

        return ResponseEntity.status(HttpStatus.OK).body(cardapioService.cardapioSaboresSalgados());
        
    }

    public ResponseEntity<List<Sabor>> cardapioDoces(){

        return ResponseEntity.status(HttpStatus.OK).body(cardapioService.cardapioSaboresDoces());

    }

}
