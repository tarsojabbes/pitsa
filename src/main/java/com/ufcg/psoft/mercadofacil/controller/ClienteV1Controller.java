package com.ufcg.psoft.mercadofacil.controller;

import com.ufcg.psoft.mercadofacil.dto.ClienteGetResponseDTO;
import com.ufcg.psoft.mercadofacil.dto.ClientePostPutRequestDTO;
import com.ufcg.psoft.mercadofacil.model.Cliente;
import com.ufcg.psoft.mercadofacil.service.cliente.ClienteAlterarService;
import com.ufcg.psoft.mercadofacil.service.cliente.ClienteCriarService;
import com.ufcg.psoft.mercadofacil.service.cliente.ClienteExcluirService;
import com.ufcg.psoft.mercadofacil.service.cliente.ClienteListarService;
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


}
