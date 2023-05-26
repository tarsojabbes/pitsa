package com.ufcg.psoft.mercadofacil.controller;

import javax.management.InvalidAttributeValueException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ufcg.psoft.mercadofacil.dto.AcompanhamentoPedidoDTO;
import com.ufcg.psoft.mercadofacil.model.Acompanhamento;
import com.ufcg.psoft.mercadofacil.service.pedido.AcompanhamentoPedidoService;
import com.ufcg.psoft.mercadofacil.service.pedido.PedidoExcluirService;
import com.ufcg.psoft.mercadofacil.service.pedido.PedidoListarService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/v1/acompanhamento",
                produces = MediaType.APPLICATION_JSON_VALUE)
public class AcompanhamentoPedidoV1Controller {

    @Autowired
    PedidoListarService pedidoListarService;

    @Autowired
    PedidoExcluirService pedidoExcluirService;

    @Autowired
    AcompanhamentoPedidoService acompanhamentoPedidoService;
    
    @GetMapping("/{idPedido}")
    public ResponseEntity<Acompanhamento> statusAtual(@PathVariable @Valid Long idPedido,
                                                 @RequestParam String codigoDeAcesso){

        return ResponseEntity.status(HttpStatus.OK).body(pedidoListarService
               .listar(idPedido, codigoDeAcesso).get(0).getAcompanhamento());
               
    }

    @PutMapping("/{idPedido}")
    public ResponseEntity<Acompanhamento> pedidoConfirmado(@PathVariable @Valid Long idPedido,
                                 @RequestBody @Valid AcompanhamentoPedidoDTO acompanhamentoPedidoDTO,
                                 @RequestParam(value="codigoDeAcessoCliente") String codigoDeAcessoCliente) throws InvalidAttributeValueException{
        
        return ResponseEntity.status(HttpStatus.OK).body(acompanhamentoPedidoService.alteraAcompanhamento(idPedido, codigoDeAcessoCliente, acompanhamentoPedidoDTO, 0));
    
    }

    @PutMapping("/{idPedido}")
    public ResponseEntity<Acompanhamento> pedidoEmPreparacao(@PathVariable @Valid Long idPedido,
                                   @RequestBody @Valid AcompanhamentoPedidoDTO acompanhamentoPedidoDTO,
                                   @RequestParam(value="codigoDeAcessoEstabelecimento") String codigoDeAcessoEstabelecimento) throws InvalidAttributeValueException{
        
        return ResponseEntity.status(HttpStatus.OK).body(acompanhamentoPedidoService.alteraAcompanhamento(idPedido, codigoDeAcessoEstabelecimento, acompanhamentoPedidoDTO, 1));

    }

    @PutMapping("/{idPedido}")
    public ResponseEntity<Acompanhamento> pedidoEnviado(@PathVariable @Valid Long idPedido,
                              @RequestBody @Valid AcompanhamentoPedidoDTO acompanhamentoPedidoDTO,
                              @RequestParam(value="codigoDeAcessoEstabelecimento") String codigoDeAcessoEstabelecimento) throws InvalidAttributeValueException{
        
        return ResponseEntity.status(HttpStatus.OK).body(acompanhamentoPedidoService.alteraAcompanhamento(idPedido, codigoDeAcessoEstabelecimento, acompanhamentoPedidoDTO, 2));
    
    }

    @PutMapping("/{idPedido}")
    public ResponseEntity<Acompanhamento> pedidoEmRota(@PathVariable @Valid Long idPedido,
                             @RequestBody @Valid AcompanhamentoPedidoDTO acompanhamentoPedidoDTO,
                             @RequestParam(value="codigoDeAcessoEstabelecimento") String codigoDeAcessoEstabelecimento) throws InvalidAttributeValueException{
        
        return ResponseEntity.status(HttpStatus.OK).body(acompanhamentoPedidoService.alteraAcompanhamento(idPedido, codigoDeAcessoEstabelecimento, acompanhamentoPedidoDTO, 3));
    
    }

    @PutMapping("/{idPedido}")
    public ResponseEntity<Acompanhamento> pedidoEntregue(@PathVariable @Valid Long idPedido,
                               @RequestBody @Valid AcompanhamentoPedidoDTO acompanhamentoPedidoDTO,
                               @RequestParam(value="codigoDeAcessoCliente") String codigoDeAcessoCliente) throws InvalidAttributeValueException{

        return ResponseEntity.status(HttpStatus.OK).body(acompanhamentoPedidoService.alteraAcompanhamento(idPedido, codigoDeAcessoCliente, acompanhamentoPedidoDTO, 4));

    }

    @DeleteMapping("/{idPedido}")
    public void cancelaPedido(@PathVariable Long idPedido,
                              @RequestParam(value = "codigoDeAcessoCliente", required = true) String codigoDeAcessoCliente){
    
        pedidoExcluirService.excluir(idPedido, codigoDeAcessoCliente);

    }

}
