package br.gov.es.conceicaodocastelo.stock.controllers.generic;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import br.gov.es.conceicaodocastelo.stock.models.generic.BaseEntity;

public interface GenericController<T extends BaseEntity> {
    ResponseEntity<Object> save(@RequestBody T entity);
    ResponseEntity<String> saveAll(@RequestBody List<T> entitys);


    ResponseEntity<List<T>> findAll() throws Exception;

    ResponseEntity<Object> findById(@PathVariable Long id);

    ResponseEntity<Boolean> delete(@PathVariable Long id);
    ResponseEntity<Boolean> delete(@RequestBody T entity);
    ResponseEntity<Boolean> deleteAll();


    
    ResponseEntity<Object> findByNameS(@PathVariable String name);

    ResponseEntity<Object> findByNameI(@PathVariable String name);

    ResponseEntity<Object> findByNameO(@PathVariable("name") String name);

}
