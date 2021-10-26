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
   $FL_ID = $_POST["FL_ID"];

    





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

        $sql_select = "SELECT * FROM friend_list WHERE ID = '{$FL_ID}';";
        $result = mysqli_query($con,$sql_select);
        $row = mysqli_fetch_array($result);
        if($row[0])
        {
            $user_ID =$row["user_ID"];
            $friend_ID =$row["friend_ID"];
        }

        //userID와 friendID가 같고
        //friendID와 userID가 같은 ID
        $sql_getID = "SELECT * FROM friend_list where user_ID='{$friend_ID}' AND friend_ID = '{$user_ID}' ; ";
        $result_id = mysqli_query($con,$sql_getID);
        $row_id = mysqli_fetch_array($result_id);
        if($row_id[0])
        {
            $FL2_ID =$row_id["ID"];
        }


       

        $sql_fl = "DELETE FROM friend_list WHERE ID = '{$FL_ID}' OR ID = '{$FL2_ID}';";
        if($con -> query($sql_fl))
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
    $response["sql_fl"] = $sql_fl;
    $response["ID2"] = $sql_getID;
    $response["FL2_ID"] = $FL2_ID;

    $response["삭제한 ID"] = $FL_ID+","+$FL2_ID;

    header('Content-Type: application/json; charset=utf8');

    echo json_encode($response);

?>