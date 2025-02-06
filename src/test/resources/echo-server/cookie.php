<?php
session_start();
setcookie("xburger", "opt1");

if(empty($_SESSION['date'])){
    $_SESSION['date'] = date('H:i:s');
}

echo "# SESSION:\n";
foreach ($_SESSION as $key => $value){
    echo $key . ': ' . $value . "\n";
}

echo "\n";

echo "# COOKIE:\n";
foreach ($_COOKIE as $key => $value){
    echo $key . ': ' . $value . "\n";
}
?>
