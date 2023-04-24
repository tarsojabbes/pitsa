package com.ufcg.psoft.mercadofacil.repository;

import com.ufcg.psoft.mercadofacil.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
}

