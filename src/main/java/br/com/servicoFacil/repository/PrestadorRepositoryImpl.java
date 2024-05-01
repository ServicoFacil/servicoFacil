package br.com.servicoFacil.repository;

import br.com.servicoFacil.model.entity.Prestador;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

public class PrestadorRepositoryImpl implements PrestadorRepositoryCustom{
    private final MongoTemplate mongoTemplate;
    @Autowired
    public PrestadorRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Page<Prestador> findByDynamicQuery(String nome, String formaPagamento, String cnpj, String categoria, Integer tempoExperiencia, Pageable pageable) {
        Query query = new Query();
        if (nome != null) {
            query.addCriteria(Criteria.where("nome").regex(nome, "i"));
        }
        if (formaPagamento != null) {
            query.addCriteria(Criteria.where("dadosServico.formaPagamento").is(formaPagamento));
        }
        if (cnpj != null) {
            query.addCriteria(Criteria.where("dadosServico.cnpj").is(cnpj));
        }
        if (categoria != null) {
            query.addCriteria(Criteria.where("dadosServico.categoria").is(categoria));
        }
        if (tempoExperiencia != null && tempoExperiencia > 0) {
            query.addCriteria(Criteria.where("dadosServico.tmpExperiencia").gte(tempoExperiencia));
        }
        List<Prestador> prestadores = mongoTemplate.find(query.with(pageable), Prestador.class);
        long totalElements = mongoTemplate.count(query, Prestador.class);
        return new PageImpl<>(prestadores, pageable, totalElements);
    }

}
