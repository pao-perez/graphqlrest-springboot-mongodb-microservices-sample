package com.paoperez.contentservice;

import java.util.Collection;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;

final class ContentMapperImpl implements ContentMapper {
    private final ModelMapper mapper;

    ContentMapperImpl(final ModelMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Content contentDtoToContent(ContentDTO contentDto) {
        return mapper.map(contentDto, Content.class);
    }

    @Override
    public ContentDTO contentToContentDto(Content content) {
        return mapper.map(content, ContentDTO.class);
    }

    @Override
    public Collection<ContentDTO> contentsToContentsDTO(Collection<Content> contents) {
        return mapList(contents, ContentDTO.class);
    }

    private <S, T> Collection<T> mapList(Collection<S> source, Class<T> targetClass) {
        return source.stream().map(element -> mapper.map(element, targetClass))
                .collect(Collectors.toList());
    }
}
