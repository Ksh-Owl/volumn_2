<?php
    require_once "DBconnection.php";

    $con = mysqli_connect($host,$db_user,$db_password,$db_name);
    if(mysqli_connect_error($con))
    {
        echo "Failed to connect to MySQL: " . mysqli_connect_error();
    
    }


    // mysqli_query($con,'SET NAMES utf8');
    $room_ID = $_POST["room_ID"];
    $member = $_POST["member"];
   // $mem_count = $_POST["mem_count"];



    // 전에 있던 방정보 받아오기

    $sql_select = " SELECT room_ID,room_nm,member,mem_count,CREATE_DATE FROM chat_room WHERE room_ID = '{$room_ID}' ;";

    $result = mysqli_query($con,$sql_select);

    $row = mysqli_fetch_array($result);
    
    
    if($row[0])
    {
        $member =  str_replace(','.$member,"",$row["member"]);
        $mem_count = $row["mem_count"]-1;
        
    }

    $sql = "UPDATE chat_room SET member='{$member}', mem_count= '{$mem_count}' where room_ID = '{$room_ID}';";
    



     

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