package com.paoperez.contentservice;

import java.util.Collection;

interface ContentService {

  /**
   * @return Collection<Content> - a collection of all contents.
   */
  Collection<Content> getAllContents();

  /**
   * 
   * @param id - The id of the content to be retrieved
   * @return Content - The content to be retrieved
   * @throws ContentNotFoundException - Thrown when the id of the content to be retrieved was not
   *                                  found.
   */
  Content getContent(String id) throws ContentNotFoundException;

  /**
   * @param content - The content to be created.
   * @return String - The ID of the created content.
   */
  String createContent(Content content);

  /**
   * 
   * @param id      - The id of the content to be updated
   * @param content - The content to be updated.
   * @throws ContentNotFoundException - Thrown when the id of the content to be updated was not
   *                                  found.
   * @throws ContentMismatchException - Thrown when the id in the argument did not match the id in
   *                                  the content argument.
   */
  void updateContent(String id, Content content)
      throws ContentNotFoundException, ContentMismatchException;

  /**
   * 
   * @param id - The id of the content to be deleted
   * @throws ContentNotFoundException - Thrown when the id of the content to be deleted was not
   *                                  found.
   */
  void deleteContent(String id) throws ContentNotFoundException;
}
