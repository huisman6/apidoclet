/*
 * Copyright 2002-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.springframework.http;
public class HttpEntity<T> {

  /**
   * The empty {@code HttpEntity}, with no body or headers.
   */
  public static final HttpEntity<?> EMPTY = new HttpEntity<Object>();



  private final T body;


  /**
   * Create a new, empty {@code HttpEntity}.
   */
  protected HttpEntity() {
    this(null);
  }

  /**
   * Create a new {@code HttpEntity} with the given body and no headers.
   * 
   * @param body the entity body
   */
  public HttpEntity(T body) {
    this.body = body;
  }

  /**
   * Returns the body of this entity.
   */
  public T getBody() {
    return this.body;
  }

  /**
   * Indicates whether this entity has a body.
   */
  public boolean hasBody() {
    return (this.body != null);
  }
}
