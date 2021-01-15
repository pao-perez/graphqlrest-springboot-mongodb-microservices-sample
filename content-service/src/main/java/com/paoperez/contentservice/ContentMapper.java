package com.paoperez.contentservice;

import java.util.Collection;

interface ContentMapper {
    Content contentDtoToContent(ContentDTO contentDto);

    ContentDTO contentToContentDto(Content content);

    Collection<ContentDTO> contentsToContentsDTO(Collection<Content> contents);
}
