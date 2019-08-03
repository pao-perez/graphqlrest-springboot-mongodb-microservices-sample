package com.paoperez.graphql;

import java.util.Collection;

import com.paoperez.graphql.models.Avatar;
import com.paoperez.graphql.models.Category;
import com.paoperez.graphql.models.Content;
import com.paoperez.graphql.models.Image;
import com.paoperez.graphql.services.AvatarService;
import com.paoperez.graphql.services.CategoryService;
import com.paoperez.graphql.services.ContentService;
import com.paoperez.graphql.services.ImageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetcher;

@Component
public class GraphQLDataFetchers {
  @Autowired
  private ContentService contentService;

  @Autowired
  private ImageService imageService;

  @Autowired
  private CategoryService categoryService;

  @Autowired
  private AvatarService avatarService;

  public DataFetcher<Collection<Content>> getAllContentsDataFetcher() {
    return dataFetchingEnvironment -> {
      return this.contentService.getAllContents();
    };
  }

  public DataFetcher<Content> getContentDataFetcher() {
    return dataFetchingEnvironment -> {
      String id = dataFetchingEnvironment.getArgument("id");
      return this.contentService.getContent(id);
    };
  }

  public DataFetcher<Image> getContentImageDataFetcher() {
    return dataFetchingEnvironment -> {
      Content content = dataFetchingEnvironment.getSource();
      String id = content.getImageId();
      return this.imageService.getImage(id);
    };
  }

  public DataFetcher<Category> getCategoryDataFetcher() {
    return dataFetchingEnvironment -> {
      Content content = dataFetchingEnvironment.getSource();
      String id = content.getCategoryId();
      return this.categoryService.getCategory(id);
    };
  }

  public DataFetcher<Avatar> getAvatarDataFetcher() {
    return dataFetchingEnvironment -> {
      Content content = dataFetchingEnvironment.getSource();
      String id = content.getAvatarId();
      return this.avatarService.getAvatar(id);
    };
  }

  public DataFetcher<Image> getAvatarImageDataFetcher() {
    return dataFetchingEnvironment -> {
      Avatar avatar = dataFetchingEnvironment.getSource();
      String id = avatar.getImageId();
      return this.imageService.getImage(id);
    };
  }

}
