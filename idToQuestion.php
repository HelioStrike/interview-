<?php
$servername = "localhost";
$username = "root";
$password = "";
$dbname = "interview_database";

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);

//select question_id
$command = "SELECT question,answer FROM questions_master WHERE id = '" . $_GET["id"] . "'";
$result = $conn->query($command);

//if such entries exist
if($result->num_rows > 0)
{
    //print out question
    echo $row["question_id"] . ",";
    
}
?>