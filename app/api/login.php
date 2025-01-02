<?php
include 'db_config.php';

// Retrieve email and password from POST request
$email = $_POST['email'] ?? null;
$password = $_POST['password'] ?? null;

// Validate that email and password are provided
if (empty($email) || empty($password)) {
    echo json_encode(["status" => "failed", "message" => "Email or password is missing"]);
    exit;
}

// Query the database to check if the user exists with the given email and password
$sql = "SELECT * FROM users WHERE users_email='$email' AND users_password='$password'";
$result = $conn->query($sql);

// Check the result of the query
if ($result->num_rows > 0) {
    echo json_encode(["status" => "success"]);
} else {
    echo json_encode(["status" => "failed", "message" => "Invalid email or password"]);
}

// Close the database connection
$conn->close();
