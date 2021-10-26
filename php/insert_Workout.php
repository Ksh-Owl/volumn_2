<?php
    require_once "DBconnection.php";

    $con = mysqli_connect($host,$db_user,$db_password,$db_name);
    if(mysqli_connect_error($con))
    {
        echo "Failed to connect to MySQL: " . mysqli_connect_error();
    
    }


    // mysqli_query($con,'SET NAMES utf8');

    $userEmail = $_POST["userEmail"];
    $workout_Part = $_POST["workout_Part"];
    $workout_Name = $_POST["workout_Name"];


    //유저이메일로 유저 번호 얻기


    $sql_select = "SELECT ID FROM user WHERE userEmail = '{$userEmail}' ;";

    $result = mysqli_query($con,$sql_select);

    $row = mysqli_fetch_array($result);
    
    
    if($row[0])
    {
        $userID =$row["ID"];
    
        
    }




    $sql ="insert into workoutList_Record(userID,workout_Part,workout_Name,CREATE_DATE) VALUES ('{$userID}','{$workout_Part}','{$workout_Name}',NOW() );";
     

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