package com.paoperez.contentservice;

import java.util.Collection;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring") // Mark as injectable bean
public interface ContentMapper {
    Content contentDtoToContent(ContentDTO contentDto);

    ContentDTO contentToContentDto(Content content);

    Collection<ContentDTO> contentsToContentsDTO(Collection<Content> contents);
}
