<?php

$servername = "localhost";
$username = "root";
$password = "";
$dbname = "interview_database";

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);

$command = "UPDATE interviews_master SET status=".$_GET["nstatus"]." WHERE interview_id=".$_GET["id"];
$conn->query($command);

?>