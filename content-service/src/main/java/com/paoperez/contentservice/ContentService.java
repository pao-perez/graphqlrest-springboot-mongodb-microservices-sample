package com.paoperez.contentservice;

import java.util.Collection;

interface ContentService {
    Collection<Content> getAllContents();

    Content getContent(String id) throws ContentNotFoundException;

    Content createContent(Content content);

    void updateContent(String id, Content content) throws ContentNotFoundException;

    void deleteContent(String id) throws ContentNotFoundException;
}
