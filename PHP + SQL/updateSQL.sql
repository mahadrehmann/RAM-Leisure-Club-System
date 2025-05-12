UPDATE profile_images
SET image_path = REPLACE(image_path,
    'http://192.168.94.111/RAMsolutions/',
    'http://192.168.43.111/RAMsolutions/'
);
