package com.paoperez.contentservice;

import java.util.Collection;

interface ContentService {
  Collection<Content> getAllContents();

  Content getContent(String id) throws ContentNotFoundException;

  /**
   * @param Content - The content to be created.
   * @return String - The ID of the created content.
   */
  String createContent(Content content);

  /**
   * @param id      - The id of the content to be updated
   * @param content - The content to be updated.
   * @throws ContentNotFoundException - Thrown when the id of the content to be updated was not
   *                                  found.
   */
  void updateContent(String id, Content content) throws ContentNotFoundException;

  void deleteContent(String id) throws ContentNotFoundException;
}
