<?php 
 require_once "DBconnection.php";

 $con = mysqli_connect($host,$db_user,$db_password,$db_name);
 mysqli_query($con,'SET NAMES utf8');

$userEmail = $_POST["userEmail"];
$workout_Part = $_POST["workout_Part"];


if(mysqli_connect_error($con))//연결오류시 
{
    echo "Failed to connect to MySQL: " . mysqli_connect_error();

}

if($workout_Part == "전체")
{

$sql = "SELECT WR.workout_Name,WR.workout_Part,WR.workout_ID FROM user INNER JOIN workoutList_Record AS WR ON user.ID = WR.userID WHERE user.userEmail = '{$userEmail}';";


}else
{
  
$sql = "SELECT WR.workout_Name,WR.workout_Part,WR.workout_ID FROM user INNER JOIN workoutList_Record AS WR ON user.ID = WR.userID WHERE user.userEmail = '{$userEmail}' AND WR.workout_Part = '{$workout_Part}';";

}
$result = mysqli_query($con,$sql);  

//$sql = "SELECT workout_Name FROM workoutList_Record WHERE userID = '{$userEmail}' AND  workout_Part ='{$workout_Part}' ;";


//$row = mysqli_fetch_array($result);

// if($row[0])
// {
//     $response["success"] = true;
//     $response["workout_Name"] =$row["workout_Name"];
//     $response["sql"] = $sql;

//     //$response["userPw"] = $userPw;
    
// }else
// {
//     $response["success"] = false;
//     $response["sql"] = $sql;
// }
// $stmt = $con->prepare($sql);
// $stmt->execute();

// if($stmt->rowCount() >0)
// {
//     $data = array();

//     while($row=$stmt->fetch(PDO::FETCH_ASSPC))//쿼리 결과 한꺼번에 저장
//     {
//         extract($row);

//         array_push($data,array('workout_Name'=>$workout_Name));
//     }
// }


// header('Content-Type: application/json; charset=utf8');
// $json = json_encode(array("webnautes"=>$data),JSON_PRETTY_PRINT+JSON_UNESCAPED_UNICODE);


$response = array();

while($row = mysqli_fetch_array($result))
{
    
    array_push($response,array('workout_Name'=>$row["workout_Name"],'workout_Part'=>$row["workout_Part"],'workout_ID'=>$row["workout_ID"] ));
}



header('Content-Type: application/json; charset=utf8');
$json = json_encode(array("workout"=>$response), JSON_PRETTY_PRINT+JSON_UNESCPED_UNICOCDE );
echo $json;

?>