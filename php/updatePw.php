<?php
    require_once "DBconnection.php";

    $con = mysqli_connect($host,$db_user,$db_password,$db_name);
    if(mysqli_connect_error($con))
    {
        echo "Failed to connect to MySQL: " . mysqli_connect_error();
    
    }



    $userEmail = $_POST["userEmail"];
    $userPw = $_POST["userPw"];

    $sql = "UPDATE user SET userPw = SHA2('{$userPw}',256),CREATE_DATE = NOW()  WHERE userEmail ='{$userEmail}';";
     

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



    echo json_encode($response);

?>