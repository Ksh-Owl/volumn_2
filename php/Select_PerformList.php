<?php 
 require_once "DBconnection.php";

 $con = mysqli_connect($host,$db_user,$db_password,$db_name);
 mysqli_query($con,'SET NAMES utf8');

//$userEmail = $_POST["userEmail"];
//$DATE = $_POST["DATE"];
$SET_ID = $_POST["SET_ID"];


if(mysqli_connect_error($con))//연결오류시 
{
    echo "Failed to connect to MySQL: " . mysqli_connect_error();

}


//유저ID번호 얻기

// $sql_select = "SELECT ID FROM user WHERE userEmail = '{$userEmail}' ;";

// $result = mysqli_query($con,$sql_select);

// $row = mysqli_fetch_array($result);


// if($row[0])
// {
//     $userID =$row["ID"];

    
// }


  
$sql = "SELECT Perform_ID,Perform_set,Perform_count,Perform_weight FROM perform_Record WHERE set_ID = '{$SET_ID}';";


$result = mysqli_query($con,$sql);  


$response = array();

while($row = mysqli_fetch_array($result))
{
    
    array_push($response,array('Perform_ID'=>$row["Perform_ID"],'Perform_set'=>$row["Perform_set"],'Perform_count'=>$row["Perform_count"],'Perform_weight'=>$row["Perform_weight"] ));
}



header('Content-Type: application/json; charset=utf8');
$json = json_encode(array("workout"=>$response), JSON_PRETTY_PRINT+JSON_UNESCPED_UNICOCDE );
echo $json;

?>