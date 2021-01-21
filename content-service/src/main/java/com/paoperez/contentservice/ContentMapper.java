package com.paoperez.contentservice;

import java.util.Collection;

interface ContentMapper {
    Content contentDtoToContent(ContentDTO contentDto);

    ContentDTO contentToContentDto(Content content);

    Collection<ContentDTO> contentsToContentDTOs(Collection<Content> contents);
}
