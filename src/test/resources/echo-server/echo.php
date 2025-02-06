<?php
header("Content-Type: application/json; charset=UTF-8");

$inputContent = file_get_contents('php://input');
$inputContent = substr($inputContent, 0, 100);

echo json_encode(array(
    "query-params" => $_GET,
    "post-params" => $_POST,
    "files" => $_FILES,
    "cookies" => $_COOKIE,
    "headers" => apache_request_headers(),
    "body" => base64_encode($inputContent),
    "server" => $_SERVER,
), JSON_PRETTY_PRINT);
