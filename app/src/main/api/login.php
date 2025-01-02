<?php
include 'db_config.php';

$email = $_POST['users_email'];
$password = $_POST['users_password'];

$sql = "SELECT * FROM users WHERE users_email='$email' AND users_password='$password'";
$result = $conn->query($sql);

if ($result->num_rows > 0) {
    echo json_encode(["status" => "success"]);
} else {
    echo json_encode(["status" => "failed"]);
}

$conn->close();
?>
