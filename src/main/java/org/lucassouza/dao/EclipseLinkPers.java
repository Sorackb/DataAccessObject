package org.lucassouza.dao;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

/**
 *
 * @author Lucas Souza <sorackb@gmail.com>
 * @param <A>
 */
public class EclipseLinkPers<A> implements BasicPers<A> {

  protected EntityManagerFactory entityManagerFactory;
  protected EntityManager entityManager;
  protected Class<A> objectClass;

  public EclipseLinkPers(String persistenceUnitName, HashMap<String, String> properties) {
    this.entityManagerFactory = Persistence.createEntityManagerFactory(
            persistenceUnitName, properties);
    this.entityManager = this.entityManagerFactory.createEntityManager();
  }

  public void create(A object) {
    this.entityManager.getTransaction().begin();
    this.entityManager.persist(object);
    this.entityManager.getTransaction().commit();
  }

  public A read(Object id) {
    return this.entityManager.find(this.objectClass, id);
  }

  @SuppressWarnings("unchecked")
  public A read(LinkedHashMap<String, Object> condition,
          LinkedHashMap<String, String> order) {
    String sql = this.buildQuery(condition, order);
    List<A> queryResult;
    Query query;

    query = this.entityManager.createQuery(sql);
    queryResult = query.setFirstResult(0).setMaxResults(1).getResultList();

    if (!queryResult.isEmpty()) {
      return queryResult.get(0);
    } else {
      return null;
    }
  }

  @SuppressWarnings("unchecked")
  public List<A> readList(LinkedHashMap<String, Object> condition,
          LinkedHashMap<String, String> order) {
    String sql = this.buildQuery(condition, order);
    List<A> result;
    Query query;

    query = this.entityManager.createQuery(sql);
    this.setParameter(query, condition);
    result = query.getResultList();

    return result;
  }

  @SuppressWarnings("unchecked")
  public List<A> readAll(LinkedHashMap<String, String> order) {
    String sql = this.buildQuery(null, order);
    List<A> result;
    Query query;

    query = this.entityManager.createQuery(sql);
    result = query.getResultList();

    return result;
  }

  public void update(A object) {
    this.entityManager.getTransaction().begin();
    this.entityManager.merge(object);
    this.entityManager.getTransaction().commit();
  }

  public void delete(A object) {
    this.entityManager.getTransaction().begin();
    this.entityManager.remove(object);
    this.entityManager.getTransaction().commit();
  }

  public String buildQuery(LinkedHashMap<String, Object> condition,
          LinkedHashMap<String, String> order) {
    String result = "select x from " + this.objectClass.getSimpleName() + " x";
    String sqlCondition = this.buildCondition(condition);
    String sqlOrder = this.buildOrder(order);

    if (!sqlCondition.equals("")) {
      result = result + " " + sqlCondition;
    }

    if (!sqlOrder.equals("")) {
      result = result + " " + sqlOrder;
    }

    return result;
  }

  public void setParameter(Query query, LinkedHashMap<String, Object> condition) {
    if (condition != null && !condition.isEmpty()) {
      for (String chave : condition.keySet()) {
        query.setParameter("p" + chave.toLowerCase(), condition.get(chave));
      }
    }
  }

  public String buildCondition(LinkedHashMap<String, Object> condition) {
    StringBuilder result = new StringBuilder();

    if (condition != null && !condition.isEmpty()) {
      for (String key : condition.keySet()) {
        if (result.length() != 0) {
          result.append(" and ");
        }

        result.append("x.");
        result.append(key);
        result.append(" = :p");
        result.append(key.toLowerCase());
      }

      return "where " + result.toString();
    }

    return result.toString();
  }

  public String buildOrder(LinkedHashMap<String, String> order) {
    StringBuilder result = new StringBuilder();

    if (order != null && !order.isEmpty()) {
      for (String key : order.keySet()) {
        String content = order.get(key);

        if (result.length() != 0) {
          result.append(", ");
        }

        result.append("x.");
        result.append(key);

        if (content != null && !content.equals("")) {
          result.append(" ");
          result.append(content);
        }
      }

      return "order by " + result.toString();
    }

    return result.toString();
  }
}
