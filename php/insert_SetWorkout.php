<?php
    require_once "DBconnection.php";

    $con = mysqli_connect($host,$db_user,$db_password,$db_name);
    if(mysqli_connect_error($con))
    {
        echo "Failed to connect to MySQL: " . mysqli_connect_error();
    
    }


    // mysqli_query($con,'SET NAMES utf8');

    $workout_ID = $_POST["workout_ID"];
    $DATE = $_POST["date"];


    //유저이메일로 유저 번호 얻기


    // $sql_select = "SELECT ID FROM user WHERE userEmail = '{$userEmail}' ;";

    // $result = mysqli_query($con,$sql_select);

    // $row = mysqli_fetch_array($result);
    
    
    // if($row[0])
    // {
    //     $userID =$row["ID"];
    
        
    // }




    $sql ="insert into set_list(workout_ID,DATE,CREATE_DATE) VALUES ('{$workout_ID}','{$DATE}',NOW() );";
     

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
    $response["sql"] = $sql;

    header('Content-Type: application/json; charset=utf8');

    echo json_encode($response);

?>