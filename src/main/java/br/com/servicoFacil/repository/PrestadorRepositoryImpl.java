package br.com.servicoFacil.repository;

import br.com.servicoFacil.model.entity.Prestador;
import org.springframework.beans.factory.annotation.Autowired;
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
    public List<Prestador> findByCriteria(String criterio) {
        Query query = new Query();
        Criteria criteria = new Criteria().orOperator(
                Criteria.where("cpf").is(criterio),
                Criteria.where("dadosServico.cnpj").is(criterio),
                Criteria.where("nome").regex(criterio, "i"),
                Criteria.where("dadosServico.categoria").in(criterio)
        );
        query.addCriteria(criteria);
        return mongoTemplate.find(query, Prestador.class);
    }

}
