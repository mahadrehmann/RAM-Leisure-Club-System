<?php
header('Content-Type: application/json');
$mysqli = new mysqli("localhost","root","","ram");
if ($mysqli->connect_errno) {
    echo json_encode(["status"=>"error","message"=>"DB connect failed"]);
    exit;
}

if (!isset($_GET['user_id'])) {
    echo json_encode(["status"=>"error","message"=>"Missing user_id"]);
    exit;
}

$userId = $_GET['user_id'];
$stmt = $mysqli->prepare("SELECT image_path FROM profile_images WHERE user_id=?");
$stmt->bind_param("s", $userId);
$stmt->execute();
$stmt->bind_result($path);
if ($stmt->fetch()) {
    echo json_encode(["status"=>"success","image_url"=>$path]);
} else {
    echo json_encode(["status"=>"error","message"=>"No image found"]);
}
$stmt->close();
$mysqli->close();
?>
