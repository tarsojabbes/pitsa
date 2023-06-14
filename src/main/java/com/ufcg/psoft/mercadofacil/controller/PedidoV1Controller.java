package com.ufcg.psoft.mercadofacil.controller;

import com.ufcg.psoft.mercadofacil.dto.PedidoPostPutRequestDTO;
import com.ufcg.psoft.mercadofacil.model.Pedido;
import com.ufcg.psoft.mercadofacil.service.pedido.*;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(
        value = "/v1/pedidos",
        produces = MediaType.APPLICATION_JSON_VALUE)
public class PedidoV1Controller {

    @Autowired
    PedidoAlterarService pedidoAlterarService;

    @Autowired
    PedidoConfirmarPagamentoService pedidoConfirmarPagamentoService;

    @Autowired
    PedidoCriarService pedidoCriarService;

    @Autowired
    PedidoListarService pedidoListarService;

    @Autowired
    PedidoExcluirService pedidoExcluirService;

    @Autowired
    PedidoIndicarProntoService pedidoIndicarProntoService;

    @Autowired
    PedidoAtribuirEntregadorService pedidoAtribuirEntregadorService;

    @Autowired
    ModelMapper modelMapper;

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> buscarPedido(@PathVariable @Valid Long id,
                                               @RequestParam(value = "codigoDeAcesso") String codigoDeAcessoCliente) {

        return ResponseEntity.status(HttpStatus.OK).body(pedidoListarService.listar(id, codigoDeAcessoCliente).get(0));
    }

    @PostMapping()
    public ResponseEntity<Pedido> criarPedido(@RequestBody @Valid PedidoPostPutRequestDTO pedidoPostPutRequestDTO,
                                              @RequestParam(value = "codigoDeAcesso") String codigoDeAcesso) {

        return ResponseEntity.status(HttpStatus.OK).body(pedidoCriarService.criar(codigoDeAcesso, pedidoPostPutRequestDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pedido> atualizarPedido(@PathVariable @Valid Long id,
                                                  @RequestBody @Valid PedidoPostPutRequestDTO pedidoPostPutRequestDTO,
                                                  @RequestParam(value = "codigoDeAcesso") String codigoDeAcesso) {

        return ResponseEntity.status(HttpStatus.OK).body(pedidoAlterarService.alterar(id, codigoDeAcesso, pedidoPostPutRequestDTO));
    }

    @PutMapping("/{id}/confirmarPagamento")
    public ResponseEntity<Pedido> confirmarPedido(@PathVariable @Valid Long id,
                                                  @RequestBody @Valid PedidoPostPutRequestDTO pedidoPostPutRequestDTO,
                                                  @RequestParam(value = "codigoDeAcesso") String codigoDeAcesso) {
        return ResponseEntity.status(HttpStatus.OK).body(pedidoConfirmarPagamentoService.confirmar(id, codigoDeAcesso, pedidoPostPutRequestDTO));
    }

    @DeleteMapping("/{id}")
    public void excluirPedido(@PathVariable @Valid Long id) {
        pedidoExcluirService.excluir(id);
    }

    @PatchMapping("/{id}/pedido-pronto")
    public ResponseEntity<Pedido> indicarPedidoPronto(@PathVariable @Valid Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(pedidoIndicarProntoService.indicarPedidoPronto(id));
    }

    @PatchMapping("/{id}/atribuir-entregador")
    public ResponseEntity<Pedido> atribuirEntregador(@PathVariable @Valid Long id,
                                                     @RequestParam(value = "idEntregador", required = true) Long idEntregador) {
        return ResponseEntity.status(HttpStatus.OK).body(pedidoAtribuirEntregadorService.atribuirEntregador(id, idEntregador));
    }

}
