package com.paoperez.imageservice;

import java.util.Collection;

interface ImageService {
  /**
   * 
   * @return Collection<Image> - a collection of all images.
   */
  Collection<Image> getAllImages();

  /**
   * 
   * @param id - The id of the image to be retrieved
   * @return Image - The image to be retrieved
   * @throws ImageNotFoundException - Thrown when the id of the image to be retrieved was not found.
   */
  Image getImage(String id) throws ImageNotFoundException;

  /**
   * 
   * @param image - The image to be created.
   * @return String - The ID of the created image.
   * @throws ImageAlreadyExistsException - Thrown when the url of the image in the argument already
   *                                     exists.
   */
  String createImage(Image image) throws ImageAlreadyExistsException;

  /**
   * 
   * @param id    - The id of the image to be updated
   * @param image - The image to be updated.
   * @throws ImageNotFoundException      - Thrown when the id of the image to be updated was not
   *                                     found.
   * @throws ImageAlreadyExistsException - Thrown when the url of the image in the argument already
   *                                     exists.
   * @throws ImageMismatchException      - Thrown when the id in the argument did not match the id
   *                                     in the image argument.
   */
  void updateImage(String id, Image image)
      throws ImageNotFoundException, ImageAlreadyExistsException, ImageMismatchException;

  /**
   * 
   * @param id - The id of the image to be deleted
   * @throws ImageNotFoundException - Thrown when the id of the image to be deleted was not found.
   */
  void deleteImage(String id) throws ImageNotFoundException;
}
