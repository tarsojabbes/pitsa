package com.ufcg.psoft.mercadofacil.controller;

import org.modelmapper.ModelMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ufcg.psoft.mercadofacil.dto.PedidoPostPutRequestDTO;
import com.ufcg.psoft.mercadofacil.model.Pedido;
import com.ufcg.psoft.mercadofacil.service.pedido.PedidoAlterarService;
import com.ufcg.psoft.mercadofacil.service.pedido.PedidoCriarService;
import com.ufcg.psoft.mercadofacil.service.pedido.PedidoExcluirService;
import com.ufcg.psoft.mercadofacil.service.pedido.PedidoListarService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(
                value = "/v1/pedidos",
                produces = MediaType.APPLICATION_JSON_VALUE)
public class PedidoV1Controller {
    
    @Autowired
    PedidoAlterarService pedidoAlterarService;

    @Autowired
    PedidoCriarService pedidoCriarService;

    @Autowired
    PedidoListarService pedidoListarService;

    @Autowired
    PedidoExcluirService pedidoExcluirService;

    @Autowired
    ModelMapper modelMapper;

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> buscarPedido(@PathVariable @Valid Long id,
                                               @Valid String codigoDeAcessoCliente,
                                               @ Valid PedidoPostPutRequestDTO pedidoPostPutRequestDTO) {

        return ResponseEntity.status(HttpStatus.OK).body(pedidoListarService.listar(id, codigoDeAcessoCliente, pedidoPostPutRequestDTO).get(0));
        
    }

    @PostMapping()
    public ResponseEntity<Pedido> criarPedido(@RequestBody @Valid PedidoPostPutRequestDTO pedidoPostPutRequestDTO,
                                              @RequestParam(value = "codigoDeAcesso", required = true) String codigoDeAcesso) {

        return ResponseEntity.status(HttpStatus.OK).body(pedidoCriarService.criar(codigoDeAcesso, pedidoPostPutRequestDTO));

    }

    @PutMapping("/{id}")
    public ResponseEntity<Pedido> atualizarPedido(@PathVariable Long id, 
                                                  @RequestBody @Valid PedidoPostPutRequestDTO pedidoPostPutRequestDTO,
                                                  @RequestParam(value = "codigoDeAcesso", required = true) String codigoDeAcesso) {

        return ResponseEntity.status(HttpStatus.OK).body(pedidoAlterarService.alterar(id, codigoDeAcesso, pedidoPostPutRequestDTO));

    }

    @DeleteMapping("/{id}")
    public void excluirPedido(@PathVariable Long id,
                              @RequestParam(value = "codigoDeAcesso", required = true) String codigoDeAcesso) {

        pedidoExcluirService.excluir(id, codigoDeAcesso);
    }
}
