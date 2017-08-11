package org.lucassouza.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Lucas Souza [sorackb@gmail.com]
 * @param <A extends Serializable>
 */
public interface BasicPT<A extends Serializable> {

  void create(A object);

  void create(List<A> objectList);

  A read(Object id);

  A read(Map<String, Object> condition, Map<String, String> order);

  List<A> readList(Map<String, Object> condition, Map<String, String> order);

  List<A> readAll(Map<String, String> order);

  void update(A object);

  void delete(A object);
}
