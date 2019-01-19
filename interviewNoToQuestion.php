<?php
$servername = "localhost";
$username = "root";
$password = "";
$dbname = "interview_database";

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);
// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
} 
function getAns($stuid){
    global $conn;
    $sql = "SELECT bank_id from interviews_master where interview_id=".$stuid;
    $result = $conn->query($sql);

    if ($result->num_rows > 0) {
        // output data of each row
        $row = $result->fetch_assoc();
        echo $row["bank_id"];
    } else {
        echo "no interview scheduled for this roll number";
    }
}
getAns($_GET["interview"]);
$conn->close();
?>