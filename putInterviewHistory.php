<?php

$servername = "localhost";
$username = "root";
$password = "";
$dbname = "interview_database";

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);

$command = "INSERT INTO interview_history(interview_id, question_id, student_id, question, correct_answer, response_answer, grade) VALUES("
        .$_GET["interview_id"].",".$_GET["question_id"].",".$_GET["student_id"].",".$_GET["question"].",".$_GET["correct_answer"]
        .",".$_GET["response_answer"].",".$_GET["grade"].")";
$conn->query($command);

?>