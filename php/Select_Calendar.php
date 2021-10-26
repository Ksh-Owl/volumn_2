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


// $sql = "
// SELECT WR.workout_ID,WR.workout_Part,WR.workout_Name ,SL.DATE,MAX(PR.Perform_set) AS Perform_set ,SUM(PR.Perform_count) AS Perform_count,SUM(PR.Perform_weight) AS Perform_weight,
// SUM(PR.Perform_count * PR.Perform_weight) AS VOLUMN   FROM workoutList_Record AS WR INNER JOIN set_list AS SL
// ON WR.workout_ID = SL.workout_ID
// LEFT JOIN perform_Record AS PR
// ON SL.ID = PR.set_ID

// WHERE WR.userID = '{$userID}' AND SL.DATE = '{$DATE}'

// GROUP BY WR.workout_ID ;";

$sql ="SELECT WR.workout_ID,WR.workout_Part,WR.workout_Name ,SL.DATE, IFNULL(MAX(PR.Perform_set),0)  AS Perform_set ,IFNULL(SUM(PR.Perform_count),0) AS Perform_count,IFNULL(SUM(PR.Perform_weight),0) AS Perform_weight,IFNULL(SUM(PR.Perform_count * PR.Perform_weight),0)  AS VOLUMN   FROM workoutList_Record AS WR INNER JOIN set_list AS SL
ON WR.workout_ID = SL.workout_ID
LEFT JOIN perform_Record AS PR
ON SL.ID = PR.set_ID

WHERE WR.userID = '{$userID}' AND SL.DATE = '{$DATE}'

GROUP BY WR.workout_ID ;";



$result = mysqli_query($con,$sql);  




$response = array();

while($row = mysqli_fetch_array($result))
{
    
    array_push($response,array('workout_Name'=>$row["workout_Name"],'workout_Part'=>$row["workout_Part"],'Perform_set'=>$row["Perform_set"],'Perform_count'=>$row["Perform_count"],'Perform_weight'=>$row["Perform_weight"],'VOLUMN'=>$row["VOLUMN"] ));
}



header('Content-Type: application/json; charset=utf8');
$json = json_encode(array("workout"=>$response), JSON_PRETTY_PRINT+JSON_UNESCPED_UNICOCDE );
echo $json;

?>