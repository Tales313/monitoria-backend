package br.com.monitoria.util.validators;

import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class ValorUnicoValidador implements ConstraintValidator<ValorUnico, String> {

    private String atributo;
    private Class<?> classe;
    @PersistenceContext
    private EntityManager em;

    @Override
    public void initialize(ValorUnico constraintAnnotation) {
        atributo = constraintAnnotation.nomeDoCampo();
        classe = constraintAnnotation.classe();
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        Query query = em.createQuery("select 1 from " + classe.getName() + " where " + atributo + " = :value");
        query.setParameter("value", s);
        List<?> list = query.getResultList();
        Assert.state(list.size() <= 1, "Foi encontrado mais de um " + classe + " com o atributo " + atributo);

        return list.isEmpty();
    }

}