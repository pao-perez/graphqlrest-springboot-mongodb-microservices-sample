package com.paoperez.graphql;

import com.paoperez.graphql.avatar.Avatar;
import com.paoperez.graphql.avatar.AvatarService;
import com.paoperez.graphql.category.Category;
import com.paoperez.graphql.category.CategoryService;
import com.paoperez.graphql.content.Content;
import com.paoperez.graphql.content.ContentService;
import com.paoperez.graphql.image.Image;
import com.paoperez.graphql.image.ImageService;

import graphql.schema.DataFetcher;

import java.util.Collection;

import org.springframework.stereotype.Component;


@Component
class GraphQlDataFetchers {
  private final ContentService contentService;
  private final ImageService imageService;
  private final CategoryService categoryService;
  private final AvatarService avatarService;

  GraphQlDataFetchers(final ContentService contentService, final ImageService imageService,
      final CategoryService categoryService, final AvatarService avatarService) {
    this.contentService = contentService;
    this.imageService = imageService;
    this.categoryService = categoryService;
    this.avatarService = avatarService;
  }

  DataFetcher<Collection<Content>> getAllContentsDataFetcher() {
    return dataFetchingEnvironment -> {
      return this.contentService.getAllContents();
    };
  }

  DataFetcher<Content> getContentDataFetcher() {
    return dataFetchingEnvironment -> {
      String id = dataFetchingEnvironment.getArgument("id");
      return this.contentService.getContent(id);
    };
  }

  DataFetcher<Image> getContentImageDataFetcher() {
    return dataFetchingEnvironment -> {
      Content content = dataFetchingEnvironment.getSource();
      String id = content.getImageId();
      return this.imageService.getImage(id);
    };
  }

  DataFetcher<Category> getCategoryDataFetcher() {
    return dataFetchingEnvironment -> {
      Content content = dataFetchingEnvironment.getSource();
      String id = content.getCategoryId();
      return this.categoryService.getCategory(id);
    };
  }

  DataFetcher<Avatar> getAvatarDataFetcher() {
    return dataFetchingEnvironment -> {
      Content content = dataFetchingEnvironment.getSource();
      String id = content.getAvatarId();
      return this.avatarService.getAvatar(id);
    };
  }

  DataFetcher<Image> getAvatarImageDataFetcher() {
    return dataFetchingEnvironment -> {
      Avatar avatar = dataFetchingEnvironment.getSource();
      String id = avatar.getImageId();
      return this.imageService.getImage(id);
    };
  }

}
