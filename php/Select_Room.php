<?php 
 require_once "DBconnection.php";

 $con = mysqli_connect($host,$db_user,$db_password,$db_name);
 mysqli_query($con,'SET NAMES utf8');

//$userEmail = $_POST["userEmail"];
//$DATE = $_POST["DATE"];


if(mysqli_connect_error($con))//연결오류시 
{
    echo "Failed to connect to MySQL: " . mysqli_connect_error();

}


// //유저ID번호 얻기

// $sql_select = "SELECT ID FROM user WHERE userEmail = '{$userEmail}' ;";

// $result = mysqli_query($con,$sql_select);

// $row = mysqli_fetch_array($result);


// if($row[0])
// {
//     $userID =$row["ID"];

    
// }
  
$sql = "SELECT room_ID,room_nm,member,mem_count,CREATE_DATE FROM chat_room;";

$result = mysqli_query($con,$sql);  


$response = array();

while($row = mysqli_fetch_array($result))
{
    
    array_push($response,array('room_ID'=>$row["room_ID"],'room_nm'=>$row["room_nm"],'member'=>$row["member"],'mem_count'=>$row["mem_count"],'CREATE_DATE'=>$row["CREATE_DATE"] ));
}



header('Content-Type: application/json; charset=utf8');
$json = json_encode(array("chat"=>$response), JSON_PRETTY_PRINT+JSON_UNESCPED_UNICOCDE );
echo $json;

?>