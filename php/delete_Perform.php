<?php
    require_once "DBconnection.php";

    $con = mysqli_connect($host,$db_user,$db_password,$db_name);
    if(mysqli_connect_error($con))
    {
        echo "Failed to connect to MySQL: " . mysqli_connect_error();
    
    }


    // mysqli_query($con,'SET NAMES utf8');

   // $userEmail = $_POST["userEmail"];
   // $workout_ID = $_POST["workout_ID"];
    $Perform_ID = $_POST["Perform_ID"];





    mysqli_begin_transaction($con);
    try{
        //유저이메일로 유저 번호 얻기

        // $sql_select = "SELECT ID FROM user WHERE userEmail = '{$userEmail}' ;";
        // $result = mysqli_query($con,$sql_select);

        // $row = mysqli_fetch_array($result);
        
        
        // if($row[0])
        // {
        //     $userID =$row["ID"];
        
            
        // }


       

        $sql_PR = "DELETE FROM perform_Record WHERE Perform_ID = '{$Perform_ID}';";
        if($con -> query($sql_PR))
        {
            $state =true;
        }else
        {
            $state =false;
        }


     
        mysqli_commit($con);


    }catch(mysqli_sql_exception $exception)
    {
        mysqli_rollback($mysqli);
        
        
        throw $exception;
    }
   

   
    $response = array();
    $response["success"] = $state;
    //$response["sql_WR"] = $sql_WR;
    $response["sql_PR"] = $sql_PR;

    header('Content-Type: application/json; charset=utf8');

    echo json_encode($response);

?>