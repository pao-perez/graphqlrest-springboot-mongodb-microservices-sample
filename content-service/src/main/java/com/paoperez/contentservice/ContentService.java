package com.paoperez.contentservice;

import java.util.Collection;

interface ContentService {
  Collection<Content> getAllContents();

  Content getContent(String id) throws ContentNotFoundException;

  /**
   * @param Content - The content to be created.
   * @return Content - The created content from the param with timestamp and generated ID.
   * @throws IllegalStateException - Thrown when withCreated method in Content.Builder is invoked
   *     and the DateTime instance is null.
   */
  Content createContent(Content content);

  /**
   * @param id - The id of the content to be updated
   * @param content - The content to be updated.
   * @throws ContentNotFoundException - Thrown when the id of the content to be updated was not
   *     found.
   * @throws IllegalStateException - Thrown when withUpdated method in Content.Builder is invoked
   *     and the DateTime instance is null.
   */
  void updateContent(String id, Content content) throws ContentNotFoundException;

  void deleteContent(String id) throws ContentNotFoundException;
}
