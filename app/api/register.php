<?php
include 'db_config.php';

$username = $_POST['users_name'];
$email = $_POST['users_email'];
$password = $_POST['users_password'];

$sql = "INSERT INTO users (users_name, users_email, users_password) VALUES ('$username', '$email', '$password')";
if ($conn->query($sql) === TRUE) {
    echo json_encode(["status" => "success"]);
} else {
    echo json_encode(["status" => "failed", "error" => $conn->error]);
}

$conn->close();
?>
