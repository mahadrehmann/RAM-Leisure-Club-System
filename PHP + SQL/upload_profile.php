<?php
header('Content-Type: application/json');
// adjust these to your MySQL creds:
$mysqli = new mysqli("localhost","root","","ram");
if ($mysqli->connect_errno) {
    echo json_encode(["status"=>"error","message"=>"DB connect failed"]);
    exit;
}

if (!isset($_POST['user_id']) || !isset($_FILES['image'])) {
    echo json_encode(["status"=>"error","message"=>"Missing parameters"]);
    exit;
}

$userId = $_POST['user_id'];
$file   = $_FILES['image'];
$ext    = pathinfo($file['name'], PATHINFO_EXTENSION);
$filename = uniqid("img_") . "." . $ext;
$uploadDir = __DIR__ . "/uploads/";
if (!is_dir($uploadDir)) mkdir($uploadDir, 0755, true);

$target = $uploadDir . $filename;
if (!move_uploaded_file($file['tmp_name'], $target)) {
    echo json_encode(["status"=>"error","message"=>"Upload failed"]);
    exit;
}

// Store or update the record
$stmt = $mysqli->prepare(
    "REPLACE INTO profile_images (user_id, image_path) VALUES (?,?)"
);

//--------------------------------------------------------------
// HERE CHANGE KARNA:
$relativePath = "http://192.168.43.111/RAMsolutions/uploads/$filename";
//--------------------------------------------------------------

$stmt->bind_param("ss", $userId, $relativePath);
if ($stmt->execute()) {
    echo json_encode(["status"=>"success","image_url"=>$relativePath]);
} else {
    echo json_encode(["status"=>"error","message"=>"DB write failed"]);
}
$stmt->close();
$mysqli->close();
?>
