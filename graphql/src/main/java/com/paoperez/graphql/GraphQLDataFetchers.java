package com.paoperez.graphql;

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
  private ContentService contentClient;

  @Autowired
  private ImageService imageClient;

  @Autowired
  private CategoryService categoryClient;

  @Autowired
  private AvatarService avatarClient;

  public DataFetcher<Content> getContentByIdDataFetcher() {
    return dataFetchingEnvironment -> {
      String id = dataFetchingEnvironment.getArgument("id");
      return this.contentClient.getContent(id).block();
    };
  }

  public DataFetcher<Image> getContentImageDataFetcher() {
    return dataFetchingEnvironment -> {
      Content content = dataFetchingEnvironment.getSource();
      String id = content.getImageId();
      return this.imageClient.getImage(id).block();
    };
  }

  public DataFetcher<Category> getCategoryDataFetcher() {
    return dataFetchingEnvironment -> {
      Content content = dataFetchingEnvironment.getSource();
      String id = content.getCategoryId();
      return this.categoryClient.getCategory(id).block();
    };
  }

  public DataFetcher<Avatar> getAvatarDataFetcher() {
    return dataFetchingEnvironment -> {
      Content content = dataFetchingEnvironment.getSource();
      String id = content.getAvatarId();
      return this.avatarClient.getAvatar(id).block();
    };
  }

  public DataFetcher<Image> getAvatarImageDataFetcher() {
    return dataFetchingEnvironment -> {
      Avatar avatar = dataFetchingEnvironment.getSource();
      String id = avatar.getImageId();
      return this.imageClient.getImage(id).block();
    };
  }

}
