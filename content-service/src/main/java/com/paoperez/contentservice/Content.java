package com.paoperez.contentservice;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import com.paoperez.contentservice.util.DateTimeFactory;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Document(collection = "Content")
class Content {
  @Id
  private String id;
  private String created;
  private String updated;

  @NotBlank(message = "title must not be blank")
  private String title;

  @NotBlank(message = "body must not be blank")
  private String body;

  @NotNull(message = "rank must not be empty")
  @Positive(message = "rank must be a positive number")
  private Integer rank;

  @NotBlank(message = "categoryId must not be blank")
  private String categoryId;

  @NotBlank(message = "avatarId must not be blank")
  private String avatarId;

  @NotBlank(message = "imageId must not be blank")
  private String imageId;

  static class Builder {
    private String id;
    private String created;
    private String updated;
    private String title;
    private String body;
    private Integer rank;
    private String categoryId;
    private String avatarId;
    private String imageId;
    private DateTimeFactory dateTimeFactory;

    Builder() {
    }

    Builder(final DateTimeFactory dateTimeFactory) {
      this.dateTimeFactory = dateTimeFactory;
    }

    Builder from(final Content content) {
      this.id = content.getId();
      this.created = content.getCreated();
      this.updated = content.getUpdated();
      this.title = content.getTitle();
      this.body = content.getBody();
      this.rank = content.getRank();
      this.categoryId = content.getCategoryId();
      this.avatarId = content.getAvatarId();
      this.imageId = content.getImageId();

      return this;
    }

    Builder id(String id) {
      this.id = id;
      return this;
    }

    Builder created(String created) {
      this.created = created;
      return this;
    }

    Builder updated(String updated) {
      this.updated = updated;
      return this;
    }

    Builder title(String title) {
      this.title = title;
      return this;
    }

    Builder body(String body) {
      this.body = body;
      return this;
    }

    Builder rank(Integer rank) {
      this.rank = rank;
      return this;
    }

    Builder categoryId(String categoryId) {
      this.categoryId = categoryId;
      return this;
    }

    Builder avatarId(String avatarId) {
      this.avatarId = avatarId;
      return this;
    }

    Builder imageId(String imageId) {
      this.imageId = imageId;
      return this;
    }

    Builder withCreated() throws IllegalStateException {
      if (dateTimeFactory == null) {
        throw new IllegalStateException(
            "Failed to create DateTime instance, DateTimeFactory in Builder is null.");
      }

      this.created = dateTimeFactory.dateTime();
      return this;
    }

    Builder withUpdated() throws IllegalStateException {
      if (dateTimeFactory == null) {
        throw new IllegalStateException(
            "Failed to update DateTime instance, DateTimeFactory in Builder is null.");
      }

      this.updated = dateTimeFactory.dateTime();
      return this;
    }

    Content build() {
      return new Content(id, created, updated, title, body, rank, categoryId, avatarId, imageId);
    }
  }
}
