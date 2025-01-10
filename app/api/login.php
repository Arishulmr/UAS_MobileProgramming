<?php
include 'db_config.php';

$email = $_POST['email'] ?? null;
$password = $_POST['password'] ?? null;

if (empty($email) || empty($password)) {
    echo json_encode(["status" => "failed", "message" => "Email or password is missing"]);
    exit;
}

$sql = "SELECT * FROM users WHERE users_email='$email' AND users_password='$password'";
$result = $conn->query($sql);

if ($result->num_rows > 0) {
    echo json_encode(["status" => "success"]);
} else {
    echo json_encode(["status" => "failed", "message" => "Invalid email or password"]);
}

$conn->close();
