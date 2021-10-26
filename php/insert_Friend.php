<?php
    require_once "DBconnection.php";

    $con = mysqli_connect($host,$db_user,$db_password,$db_name);
    if(mysqli_connect_error($con))
    {
        echo "Failed to connect to MySQL: " . mysqli_connect_error();
    
    }


    // mysqli_query($con,'SET NAMES utf8');

    $userEmail = $_POST["userEmail"];
    $friendEmail = $_POST["friendEmail"];
    //$friendName = $_POST["friendName"];


  


   //유저ID번호 얻기

    $sql_select = "SELECT ID FROM user WHERE userEmail = '{$userEmail}' ;";

    $result = mysqli_query($con,$sql_select);

    $row = mysqli_fetch_array($result);


    if($row[0])
    {
        $userID =$row["ID"];

        
    }
    //친구ID번호 얻기

    $sql_select2 = "SELECT ID FROM user WHERE userEmail = '{$friendEmail}' ;";

    $result2 = mysqli_query($con,$sql_select2);

    $row2 = mysqli_fetch_array($result2);


    if($row2[0])
    {
        $friend_ID =$row2["ID"];

        
    }




    $sql ="insert into friend_list(user_ID,friend_ID,CREATE_DATE) VALUES ('{$userID}', '{$friend_ID}' ,NOW() );";
     

    if($con -> query($sql))
    {
        $state =true;
    }else
    {
        $state =false;
    }




    // 


    $response = array();
    $response["success"] = $state;
    $response["sql"] = $sql;

    header('Content-Type: application/json; charset=utf8');

    echo json_encode($response);

?>