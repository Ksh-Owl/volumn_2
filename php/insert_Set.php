<?php
    require_once "DBconnection.php";

    $con = mysqli_connect($host,$db_user,$db_password,$db_name);
    if(mysqli_connect_error($con))
    {
        echo "Failed to connect to MySQL: " . mysqli_connect_error();
    
    }


    // mysqli_query($con,'SET NAMES utf8');

    $workout_ID = $_POST["workout_ID"];
    $DATE = $_POST["Date"];

    $set = $_POST["set"];
    $count =  $_POST["count"];
    $weight = $_POST["weight"];


    //운동 ID,날짜로 세트 아이디 얻기 

     $sql_select = "SELECT ID FROM set_list WHERE workout_ID = '{$workout_ID}' AND set_list.DATE = '{$DATE}' ;";

     $result = mysqli_query($con,$sql_select);

     $row = mysqli_fetch_array($result);
    
    
    if($row[0])
    {
        $set_ID =$row["ID"];
    
        
    }




    $sql ="insert into perform_Record(set_ID,Perform_set,Perform_count,Perform_weight,CREATE_DATE) VALUES ('{$set_ID}', '{$set}','{$count}' ,'{$weight}',NOW() );";
     

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