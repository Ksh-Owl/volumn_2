<?php 
 require_once "DBconnection.php";

 $con = mysqli_connect($host,$db_user,$db_password,$db_name);
 mysqli_query($con,'SET NAMES utf8');

$userEmail = $_POST["userEmail"];


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
  
//$sql = "SELECT ID,user_ID,friend_ID,friend_name,'{$userID}' AS meID FROM friend_list WHERE user_ID ='{$userID}' OR friend_ID = '{$userID}' ;";


 
$sql = "SELECT wr.userID,SUM(pr.Perform_count * pr.Perform_weight) AS volumn
FROM set_list
INNER JOIN workoutList_Record AS wr ON wr.workout_ID = set_list.workout_ID

INNER JOIN perform_Record AS pr ON set_list.ID = pr.set_ID

WHERE wr.userID = '{$userID}'
GROUP BY wr.userID ;";

$result = mysqli_query($con,$sql);  


$response = array();

while($row = mysqli_fetch_array($result))
{
    
    
    array_push($response,array('userID'=>$row["userID"],'volumn'=>$row["volumn"] ));
}



header('Content-Type: application/json; charset=utf8');
$json = json_encode(array("Rank"=>$response), JSON_PRETTY_PRINT+JSON_UNESCPED_UNICOCDE );
echo $json;

?>