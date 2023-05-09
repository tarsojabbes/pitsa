package com.ufcg.psoft.mercadofacil.repository;

import com.ufcg.psoft.mercadofacil.model.Associacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssociacaoRepository extends JpaRepository<Associacao, Long> {

    Associacao findByEntregadorIdAndEstabelecimentoId(Long entregadorId, Long estabelecimentoId);

}
