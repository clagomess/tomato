<?php
echo "Started\n\n";

$fpJson = fopen('large.json', 'w+');
fwrite($fpJson, "[");

$fpXml = fopen("large.xml", "w+");
fwrite($fpXml, "<?xml version=\"1.0\"?>\n<itens>");

$isFirstLine = true;

while (false !== ($line = fgets(STDIN))) {
    echo $line;
    $ar = explode("; ", $line);

    $refs = !empty($ar[6]) ? explode(", ", $ar[6]) : null;
    $arShortDate = explode('-', $ar[5]);
    $shortDate = array(
        "year" => (int) $arShortDate[0],
        "month" => (int) $arShortDate[1],
        "day" => (int) $arShortDate[2]
    );

    $item = array(
        "hash" => $ar[0],
        "name" => $ar[1],
        "email" => $ar[2],
        "unix-timestamp" => (int) $ar[3],
        "odd-even" => ((int) $ar[3]) % 2 == 0,
        "date" => $ar[4],
        "short-date" => $shortDate,
        "short-date-ar" => array($shortDate['year'], $shortDate['month'], $shortDate['day']),
        "refs" => $refs,
        "commit" => trim($ar[7]),
    );

    // write json
    if(!$isFirstLine) fwrite($fpJson, ",");
    fwrite($fpJson, json_encode($item));

    // write xml
    $xml = new SimpleXMLElement('<item/>');
    $xml->addChild('hash', $item['hash']);
    $xml->addChild('name', $item['name']);
    $xml->addChild('email', $item['email']);
    $xml->addChild('unix-timestamp', $item['unix-timestamp']);
    $xml->addChild('odd-even', $item['odd-even']);
    $xml->addChild('date', $item['date']);

    $shortDateChild = $xml->addChild('short-date');
    $shortDateChild->addChild('year', $item['short-date']['year']);
    $shortDateChild->addChild('month', $item['short-date']['month']);
    $shortDateChild->addChild('day', $item['short-date']['day']);

    $shortDateChild = $xml->addChild('short-date-ar');
    $shortDateChild->addChild('item', $item['short-date']['year']);
    $shortDateChild->addChild('item', $item['short-date']['month']);
    $shortDateChild->addChild('item', $item['short-date']['day']);

    if(!empty($item['refs'])){
        $refsChild = $xml->addChild('refs');
        foreach ($item['refs'] as $ref){
            $refsChild->addChild('ref', $ref);
        }
    }

    $xml->addChild('commit', $item['commit']);
    fwrite($fpXml, explode("\n", $xml->asXML(), 2)[1]);

    if($isFirstLine) $isFirstLine = false;
}

fwrite($fpJson, "]");
fclose($fpJson);

fwrite($fpXml, "</itens>");
fclose($fpXml);

echo "\n\ndone!\n";
