<?php
header("Content-Type: text/plain; charset=ISO-8859-1");

echo "## A��o A�ucar ##\n\n";

echo "# GET:\n";
foreach ($_GET as $key => $value) {
    echo $key . ": " . $value . "\n";
}
echo "\n";

echo "# POST:\n";
foreach ($_POST as $key => $value) {
    echo $key . ": " . $value . "\n";
}
echo "\n";

echo "# Body:\n";
echo file_get_contents('php://input');
