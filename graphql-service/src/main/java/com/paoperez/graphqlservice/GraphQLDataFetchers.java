package com.paoperez.graphqlservice;

import com.paoperez.graphqlservice.avatar.Avatar;
import com.paoperez.graphqlservice.avatar.AvatarService;
import com.paoperez.graphqlservice.category.Category;
import com.paoperez.graphqlservice.category.CategoryService;
import com.paoperez.graphqlservice.content.Content;
import com.paoperez.graphqlservice.content.ContentService;
import com.paoperez.graphqlservice.content.Contents;
import com.paoperez.graphqlservice.image.Image;
import com.paoperez.graphqlservice.image.ImageService;
import org.springframework.stereotype.Component;
import graphql.schema.DataFetcher;

@Component
class GraphQLDataFetchers {
  private final ContentService contentService;
  private final ImageService imageService;
  private final CategoryService categoryService;
  private final AvatarService avatarService;

  GraphQLDataFetchers(final ContentService contentService, final ImageService imageService,
      final CategoryService categoryService, final AvatarService avatarService) {
    this.contentService = contentService;
    this.imageService = imageService;
    this.categoryService = categoryService;
    this.avatarService = avatarService;
  }

  DataFetcher<Contents> getContentsDataFetcher() {
    return dataFetchingEnvironment -> {
      return this.contentService.getContents();
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
