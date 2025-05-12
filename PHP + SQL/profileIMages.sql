CREATE TABLE `profile_images` (
  `user_id` VARCHAR(128) NOT NULL PRIMARY KEY,
  `image_path` VARCHAR(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
