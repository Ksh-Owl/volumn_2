<?php
     require_once "DBconnection.php";

     $con = mysqli_connect($host,$db_user,$db_password,$db_name);
     mysqli_query($con,'SET NAMES utf8');

    $userEmail = $_POST["userEmail"];
    $userPw = $_POST["userPw"];


    if(mysqli_connect_error($con))//연결오류시 
    {
        echo "Failed to connect to MySQL: " . mysqli_connect_error();

    }
    $sql = "SELECT * FROM user WHERE userEmail = '{$userEmail}' AND  userPw LIKE SHA2('{$userPw}',256) ;";

    $result = mysqli_query($con,$sql);

    $row = mysqli_fetch_array($result);


    $response = array();
    if($row[0])
    {
        $response["success"] = true;
        $response["userEmail"] = $userEmail;
        //$response["userPw"] = $userPw;
        
    }else
    {
        $response["success"] = false;

    }



    echo json_encode($response);



?>