<?php 
 require_once "DBconnection.php";

 $con = mysqli_connect($host,$db_user,$db_password,$db_name);
 mysqli_query($con,'SET NAMES utf8');

$userEmail = $_POST["userEmail"];
$DATE = $_POST["DATE"];


if(mysqli_connect_error($con))//연결오류시 
{
    echo "Failed to connect to MySQL: " . mysqli_connect_error();

}

//유저ID번호 얻기

$sql_select = "SELECT ID FROM user WHERE userEmail = '{$userEmail}' ;";

$result = mysqli_query($con,$sql_select);

$row = mysqli_fetch_array($result);


if($row[0])
{
    $userID =$row["ID"];

    
}


$sql = "SELECT DISTINCT(SL.DATE)  FROM workoutList_Record AS WR INNER JOIN set_list AS SL
ON WR.workout_ID = SL.workout_ID
LEFT JOIN perform_Record AS PR
ON SL.ID = PR.set_ID
WHERE WR.userID = '{$userID}' AND DATE_FORMAT(SL.DATE,'%Y-%m')  = '{$DATE}';";






$result = mysqli_query($con,$sql);  




$response = array();

while($row = mysqli_fetch_array($result))
{
    
    array_push($response,array('DATE'=>$row["DATE"]));
}



header('Content-Type: application/json; charset=utf8');
$json = json_encode(array("workout"=>$response), JSON_PRETTY_PRINT+JSON_UNESCPED_UNICOCDE );
echo $json;

?>