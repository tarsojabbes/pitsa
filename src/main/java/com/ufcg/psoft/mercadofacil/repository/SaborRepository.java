package com.ufcg.psoft.mercadofacil.repository;

import com.ufcg.psoft.mercadofacil.model.Estabelecimento;
import com.ufcg.psoft.mercadofacil.model.Sabor;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface SaborRepository extends JpaRepository<Sabor, Long> {
    List<Sabor> findByEstabelecimento(Estabelecimento estabelecimento);
}