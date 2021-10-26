<?php
    require_once "DBconnection.php";

    $con = mysqli_connect($host,$db_user,$db_password,$db_name);
    if(mysqli_connect_error($con))
    {
        echo "Failed to connect to MySQL: " . mysqli_connect_error();
    
    }


    // mysqli_query($con,'SET NAMES utf8');

    $userEmail = $_POST["userEmail"];
    $userPw = $_POST["userPw"];

    //$sql ="insert into user(userEmail,userPw,userName) VALUES ('{$userEmail}', SHA2('{$userPw}',256),'{$userName}' );";
    $sql = "UPDATE user SET userPw = SHA2('{$userPw}',256),UPDATE_DATE = NOW()  WHERE userEmail ='{$userEmail}';";
     

    if($con -> query($sql))
    {
        $state =true;
    }else
    {
        $state =false;
    }




    // $statement = mysqli_prepare($con, "INSERT INTO user VALUES (?,?,?)");
    // mysqli_stmt_bind_param($statement, "sss", $userEmail, $userPw, $userName);
    // mysqli_stmt_execute($statement);


    $response = array();
    $response["success"] = $state;


    echo json_encode($response);

?>