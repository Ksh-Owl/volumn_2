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


 $sql = "SELECT * FROM ( SELECT fl.ID,fl.user_ID,u2.userName, fl.friend_ID,u1.userName AS friendName,u1.userEmail ,
 '{$userID}' AS meID 
 FROM friend_list AS fl INNER JOIN user AS u1 
 ON fl.friend_ID = u1.ID 
 INNER JOIN user AS u2
 ON fl.user_ID =u2.ID
 
 WHERE fl.user_ID ='{$userID}' OR fl.friend_ID = '{$userID}'
  ) AS fl INNER JOIN 
  ( SELECT wr.userID,SUM(pr.Perform_count * pr.Perform_weight) AS volumn
    FROM set_list
    INNER JOIN workoutList_Record AS wr ON wr.workout_ID = set_list.workout_ID
 
    INNER JOIN perform_Record AS pr ON set_list.ID = pr.set_ID
 
   
    GROUP BY wr.userID 
   ) AS vol ON fl.friend_ID = vol.userID
   ORDER BY volumn desc ;";

$result = mysqli_query($con,$sql);  


$response = array();

while($row = mysqli_fetch_array($result))
{
    
    
    array_push($response,array('ID'=>$row["ID"],'user_ID'=>$row["user_ID"],
    'userName'=>$row["userName"],'friend_ID'=>$row["friend_ID"],
    'friendName'=>$row["friendName"],'meID'=>$row["meID"] 
    ,'volumn'=>$row["volumn"] 
    ));
}



header('Content-Type: application/json; charset=utf8');
$json = json_encode(array("Rank"=>$response), JSON_PRETTY_PRINT+JSON_UNESCPED_UNICOCDE );
echo $json;

?>