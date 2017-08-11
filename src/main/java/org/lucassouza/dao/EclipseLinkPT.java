package org.lucassouza.dao;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

/**
 *
 * @author Lucas Souza [sorackb@gmail.com]
 * @param <A extends Serializable> abstract class
 */
public class EclipseLinkPT<A extends Serializable> implements BasicPT<A> {

  protected EntityManagerFactory entityManagerFactory;
  protected Class<A> objectClass;

  public EclipseLinkPT(String persistenceUnitName, Map<String, String> properties) {
    this.entityManagerFactory = Persistence.createEntityManagerFactory(
            persistenceUnitName, properties);
  }

  @Override
  public void create(A object) {
    EntityManager entityManager = this.entityManagerFactory.createEntityManager();

    entityManager.getTransaction().begin();
    entityManager.persist(object);
    entityManager.getTransaction().commit();

    entityManager.close();
  }

  @Override
  public void create(List<A> objectList) {
    EntityManager entityManager = this.entityManagerFactory.createEntityManager();

    entityManager.getTransaction().begin();

    for (A object : objectList) {
      entityManager.persist(object);
    }

    entityManager.getTransaction().commit();

    entityManager.close();
  }

  @Override
  public A read(Object id) {
    EntityManager entityManager = this.entityManagerFactory.createEntityManager();
    A result = entityManager.find(this.objectClass, id);

    return result;
  }

  @SuppressWarnings("unchecked")
  @Override
  public A read(Map<String, Object> condition,
          Map<String, String> order) {
    EntityManager entityManager = this.entityManagerFactory.createEntityManager();
    String sql = this.buildQuery(condition, order);
    List<A> queryResult;
    Query query;
    A result = null;

    query = entityManager.createQuery(sql);
    queryResult = query.setFirstResult(0).setMaxResults(1).getResultList();

    if (!queryResult.isEmpty()) {
      result = queryResult.get(0);
    }

    entityManager.close();

    return result;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<A> readList(Map<String, Object> condition, Map<String, String> order) {
    EntityManager entityManager = this.entityManagerFactory.createEntityManager();
    String sql = this.buildQuery(condition, order);
    List<A> result;
    Query query;

    query = entityManager.createQuery(sql);
    this.setParameter(query, condition);
    result = query.getResultList();
    entityManager.close();

    return result;
  }

  @SuppressWarnings("unchecked")
  public List<A> readAll(String order, Boolean asc) {
    Map<String, String> orderBy = new LinkedHashMap<>();
    String ascText = null;

    if (!asc) {
      ascText = "desc";
    }

    orderBy.put(order, ascText);
    return this.readAll(orderBy);
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<A> readAll(Map<String, String> order) {
    EntityManager entityManager = this.entityManagerFactory.createEntityManager();
    String sql = this.buildQuery(null, order);
    List<A> result;
    Query query;

    query = entityManager.createQuery(sql);
    result = query.getResultList();
    entityManager.close();

    return result;
  }

  @Override
  public void update(A object) {
    EntityManager entityManager = this.entityManagerFactory.createEntityManager();

    entityManager.getTransaction().begin();
    entityManager.merge(object);
    entityManager.getTransaction().commit();
    entityManager.close();
  }

  @Override
  public void delete(A object) {
    EntityManager entityManager = this.entityManagerFactory.createEntityManager();

    entityManager.getTransaction().begin();
    entityManager.remove(object);
    entityManager.getTransaction().commit();
    entityManager.close();
  }

  public String buildQuery(Map<String, Object> condition, Map<String, String> order) {
    String result = "select x from " + this.objectClass.getSimpleName() + " x";
    String sqlCondition = this.buildCondition(condition);
    String sqlOrder = this.buildOrder(order);

    if (!sqlCondition.isEmpty()) {
      result = result + " " + sqlCondition;
    }

    if (!sqlOrder.isEmpty()) {
      result = result + " " + sqlOrder;
    }

    return result;
  }

  public void setParameter(Query query, Map<String, Object> condition) {
    if (condition != null && !condition.isEmpty()) {
      for (String chave : condition.keySet()) {
        query.setParameter("p" + chave.toLowerCase(), condition.get(chave));
      }
    }
  }

  public String buildCondition(Map<String, Object> condition) {
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

  public String buildOrder(Map<String, String> order) {
    StringBuilder result = new StringBuilder();

    if (order != null && !order.isEmpty()) {
      for (String key : order.keySet()) {
        String content = order.get(key);

        if (result.length() != 0) {
          result.append(", ");
        }

        result.append("x.");
        result.append(key);

        if (content != null && !content.isEmpty()) {
          result.append(" ");
          result.append(content);
        }
      }

      return "order by " + result.toString();
    }

    return result.toString();
  }
}
