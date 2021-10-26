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
  
$sql = "select wr.workout_Name,wr.workout_Part,wr.workout_ID ,set_list.ID as SET_ID from set_list INNER JOIN workoutList_Record AS wr ON wr.workout_ID = set_list.workout_ID where set_list.DATE = '{$DATE}' AND wr.userID = '{$userID}';";


$result = mysqli_query($con,$sql);  


$response = array();

while($row = mysqli_fetch_array($result))
{
    
    array_push($response,array('workout_Name'=>$row["workout_Name"],'workout_Part'=>$row["workout_Part"],'workout_ID'=>$row["workout_ID"],'SET_ID'=>$row["SET_ID"] ));
}



header('Content-Type: application/json; charset=utf8');
$json = json_encode(array("workout"=>$response), JSON_PRETTY_PRINT+JSON_UNESCPED_UNICOCDE );
echo $json;

?>