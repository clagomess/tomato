<?php
header("Content-Type: application/json; charset=UTF-8");
echo json_encode(array(
    "query-params" => $_GET,
    "post-params" => $_POST,
    "files" => $_FILES,
    "cookies" => $_COOKIE,
    "headers" => apache_request_headers(),
    "body" => file_get_contents('php://input'),
    "server" => $_SERVER,
));


