package org.lucassouza.dao;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;

/**
 *
 * @author Lucas Souza [sorackb@gmail.com]
 * @param <A extends Serializable>
 */
public interface BasicPT<A extends Serializable> {

  void create(A object);

  void create(List<A> objectList);

  A read(Object id);

  A read(LinkedHashMap<String, Object> condition, LinkedHashMap<String, String> order);

  List<A> readList(LinkedHashMap<String, Object> condition, LinkedHashMap<String, String> order);

  List<A> readAll(LinkedHashMap<String, String> order);

  void update(A object);

  void delete(A object);
}
