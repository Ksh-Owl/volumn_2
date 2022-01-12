<?php
    require_once "DBconnection.php";

    $con = mysqli_connect($host,$db_user,$db_password,$db_name);
    if(mysqli_connect_error($con))
    {
        echo "Failed to connect to MySQL: " . mysqli_connect_error();
    
    }


    // mysqli_query($con,'SET NAMES utf8');

    $user_Email = $_POST["user_Email"];
    


    // //운동 ID,날짜로 세트 아이디 얻기 

    //  $sql_select = "SELECT ID FROM set_list WHERE workout_ID = '{$workout_ID}' AND set_list.DATE = '{$DATE}' ;";

    //  $result = mysqli_query($con,$sql_select);

    //  $row = mysqli_fetch_array($result);
    
    
    // if($row[0])
    // {
    //     $set_ID =$row["ID"];
    
        
    // }



    
    $sql_select ="SELECT userIMG from user  where userEmail = '{$user_Email}' ;";
   



    $result = mysqli_query($con,$sql_select);

    $row = mysqli_fetch_array($result);
    
    
    if($row[0])
    {   
        
        $state = true;
    
        $userIMG =$row["userIMG"];
    
    
        
    }else
    {
        $state =false;
    }
      
    
    
    
    
    
    
    
    
        $response = array();
        $response["success"] = $state;
        $response["img"] = $userIMG;
    
        $response["sql"] = $sql;
    
        header('Content-Type: application/json; charset=utf8');
    
        echo json_encode($response);

    // $response["success"] = $state;

    // $response["sql"] = $sql;

    // header('Content-Type: application/json; charset=utf8');

    // echo json_encode($response);

?>