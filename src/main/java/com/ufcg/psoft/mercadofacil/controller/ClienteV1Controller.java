package com.ufcg.psoft.mercadofacil.controller;

import com.ufcg.psoft.mercadofacil.dto.ClienteGetResponseDTO;
import com.ufcg.psoft.mercadofacil.dto.ClientePostPutRequestDTO;
import com.ufcg.psoft.mercadofacil.exception.*;
import com.ufcg.psoft.mercadofacil.model.Acompanhamento;
import com.ufcg.psoft.mercadofacil.model.Cliente;
import com.ufcg.psoft.mercadofacil.model.Pedido;
import com.ufcg.psoft.mercadofacil.service.cliente.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(
        value = "/v1/clientes",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class ClienteV1Controller {
    @Autowired
    ClienteAlterarService clienteAlterarService;

    @Autowired
    ClienteListarService clienteListarService;

    @Autowired
    ClienteCriarService clienteCriarService;

    @Autowired
    ClienteExcluirService clienteExcluirService;

    @Autowired
    ClienteConfirmarEntregaService clienteConfirmarEntregaService;

    @Autowired
    ClienteDemonstrarInteresseService clienteDemostrarInteresseService;

    @Autowired
    ClienteBuscarPedidoService pedidoBuscarService;

    @Autowired
    ClienteListarHistoricoPedidoService pedidoListarHistoricoService;

    @Autowired
    ClienteCancelarPedidoService clienteCancelarPedidoService;

    @GetMapping("/{id}")
    public ResponseEntity<ClienteGetResponseDTO> buscarCliente(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(clienteListarService.listar(id).get(0));
    }

    @GetMapping("")
    public ResponseEntity<List<ClienteGetResponseDTO>> buscarTodosClientes() {
        return ResponseEntity.status(HttpStatus.OK).body(clienteListarService.listar(null));
    }

    @PostMapping()
    public ResponseEntity<Cliente> criarCliente(
            @RequestBody @Valid ClientePostPutRequestDTO clientePostPutRequestDTO
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(clienteCriarService.criar(clientePostPutRequestDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cliente> atualizarCliente(
            @PathVariable Long id,
            @RequestParam(value = "codigoDeAcesso", required = true) String codigoDeAcesso,
            @RequestBody @Valid ClientePostPutRequestDTO clientePostPutRequestDTO
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(clienteAlterarService.alterar(id, codigoDeAcesso, clientePostPutRequestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluirCliente(
            @PathVariable Long id,
            @RequestParam(value = "codigoDeAcesso", required = true) String codigoDeAcesso
    ) {
        clienteExcluirService.excluir(id, codigoDeAcesso);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("");
    }


    @PostMapping("/cliente/{clienteId}/sabor/{saborId}/interesse")
    public ResponseEntity<?> demonstrarInteressePorSabor(@PathVariable Long clienteId,
                                                         @PathVariable Long saborId,
                                                         @RequestParam String codigoDeAcesso) {
        try {
            clienteDemostrarInteresseService.demonstrarInteressePorSabor(codigoDeAcesso, clienteId, saborId);
            return ResponseEntity.ok("Interesse registrado com sucesso.");
        } catch (ClienteNaoExisteException | SaborNaoExisteException | ClienteNaoAutorizadoException |
                 SaborDisponivelException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/confirmar-entrega")
    public ResponseEntity<Pedido> confirmarPedidoEntregue(@PathVariable @Valid Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(clienteConfirmarEntregaService.confirmarPedidoEntregue(id));
    }


    @GetMapping("{clienteId}/getPedido/{pedidoId}")
    public ResponseEntity<Pedido> getPedido(@PathVariable Long clienteId,
                                            @PathVariable Long pedidoId,
                                            @RequestParam String codigoDeAcessoCliente) {
        return ResponseEntity.status(HttpStatus.OK).body(pedidoBuscarService.buscaPedido(clienteId, pedidoId, codigoDeAcessoCliente));
    }

    @GetMapping("/{clienteId}/getHistoricoPedidos")
    public ResponseEntity<List<Pedido>> getHistoricoPedido(@PathVariable Long clienteId,
                                                           @RequestParam String codigoDeAcessoCliente,
                                                           @RequestParam(required = false) Acompanhamento filtroDeAcompanhamento) {
        return ResponseEntity.status(HttpStatus.OK).body(pedidoListarHistoricoService.listarHistorico(clienteId, codigoDeAcessoCliente, filtroDeAcompanhamento));
    }

    @DeleteMapping("/cancelar-pedido/{idPedido}")
    public ResponseEntity<?> cancelarPedido(@PathVariable @Valid Long idPedido,
                                            @RequestParam(required = true, value = "codigoDeAcesso") String codigoDeAcesso) {
        try {
            clienteCancelarPedidoService.cancelarPedido(idPedido, codigoDeAcesso);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("");
        } catch (PedidoNaoExisteException | ClienteNaoAutorizadoException | MudancaDeStatusInvalidaException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
