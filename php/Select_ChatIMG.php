<?php 
 require_once "DBconnection.php";

 $con = mysqli_connect($host,$db_user,$db_password,$db_name);
 mysqli_query($con,'SET NAMES utf8');

//$userEmail = $_POST["userEmail"];
$img_ID = $_POST["img_ID"];
//$room_ID = $_POST["room_ID"];


if(mysqli_connect_error($con))//연결오류시 
{
    echo "Failed to connect to MySQL: " . mysqli_connect_error();

}



$sql_select = "SELECT IMG FROM upload_img WHERE upload_img.ID ='{$img_ID}'  ;";

$result = mysqli_query($con,$sql_select);

$row = mysqli_fetch_array($result);


if($row[0])
{   
    
    $state = true;

    $IMG =$row["IMG"];


    
}else
{
    $state =false;
}
  








    $response = array();
    $response["success"] = $state;
    $response["img"] = $IMG;

    $response["sql"] = $sql;

    header('Content-Type: application/json; charset=utf8');

    echo json_encode($response);

?>