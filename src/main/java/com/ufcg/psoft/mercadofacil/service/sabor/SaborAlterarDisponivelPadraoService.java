package com.ufcg.psoft.mercadofacil.service.sabor;


import com.ufcg.psoft.mercadofacil.dto.SaborAlterarDisponivelDTO;
import com.ufcg.psoft.mercadofacil.exception.EstabelecimentoNaoAutorizadoException;
import com.ufcg.psoft.mercadofacil.exception.EstabelecimentoNaoExisteException;
import com.ufcg.psoft.mercadofacil.exception.SaborNaoExisteException;
import com.ufcg.psoft.mercadofacil.model.Estabelecimento;
import com.ufcg.psoft.mercadofacil.model.Sabor;
import com.ufcg.psoft.mercadofacil.repository.EstabelecimentoRepository;
import com.ufcg.psoft.mercadofacil.repository.SaborRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SaborAlterarDisponivelPadraoService implements SaborAlterarDisponivelService{

    @Autowired
    SaborRepository saborRepository;

    @Autowired
    EstabelecimentoRepository estabelecimentoRepository;

    @Autowired
    SaborNotificarService saborNotificarService;

    @Override
    public Sabor alterar(Long id, String codigoDeAcesso, SaborAlterarDisponivelDTO saborAlterarDisponivelDTO) {
        Sabor sabor = saborRepository.findById(id).orElseThrow(SaborNaoExisteException::new);
        Estabelecimento estabelecimento = estabelecimentoRepository.findById(sabor.getEstabelecimento().getId())
                                            .orElseThrow(EstabelecimentoNaoExisteException::new);

        if (!estabelecimento.getCodigoDeAcesso().equals(codigoDeAcesso)) {
            throw new EstabelecimentoNaoAutorizadoException();
        }

        sabor.setDisponivel(saborAlterarDisponivelDTO.isDisponivel());

        // Notifica somente quando sabor se torna disponível
        if (sabor.getDisponivel()) {
            saborNotificarService.notificar(id);
        }

        return saborRepository.save(sabor);
    }
}
