<?php 
 require_once "DBconnection.php";

 $con = mysqli_connect($host,$db_user,$db_password,$db_name);
 mysqli_query($con,'SET NAMES utf8');

$userEmail = $_POST["userEmail"];
$DATE1 = $_POST["DATE1"];
$DATE2 = $_POST["DATE2"];

$exDATE1 = $_POST["exDATE1"];
$exDATE2 = $_POST["exDATE2"];

if(mysqli_connect_error($con))//연결오류시 
{
    echo "Failed to connect to MySQL: " . mysqli_connect_error();

}


//유저ID번호 얻기

$sql_select = "SELECT ID FROM user WHERE userEmail = '{$userEmail}' ;";

$result = mysqli_query($con,$sql_select);

$row = mysqli_fetch_array($result);


if($row[0])
{
    $userID =$row["ID"];

    
}


$sql = " SELECT workout_Part, SUM(volumn) AS volumn 
        FROM (
                (
                    SELECT wr.workout_Part,SUM(pr.Perform_count * pr.Perform_weight) AS volumn
                    FROM set_list
                    INNER JOIN workoutList_Record AS wr ON wr.workout_ID = set_list.workout_ID

                    INNER JOIN perform_Record AS pr ON set_list.ID = pr.set_ID

                    WHERE set_list.DATE between '{$DATE1}' AND '{$DATE2}' AND wr.userID = '{$userID}'
                    GROUP BY wr.workout_Part 
                )   
            
            UNION
                (
                SELECT part AS workout_Part ,0 AS volumn FROM workout_Part
                )
            )AS t 
            GROUP BY workout_Part ;";

$exsql =  " SELECT workout_Part, SUM(volumn) AS volumn 
            FROM (
                    (
                        SELECT wr.workout_Part,SUM(pr.Perform_count * pr.Perform_weight) AS volumn
                        FROM set_list
                        INNER JOIN workoutList_Record AS wr ON wr.workout_ID = set_list.workout_ID

                        INNER JOIN perform_Record AS pr ON set_list.ID = pr.set_ID

                        WHERE set_list.DATE between '{$exDATE1}' AND '{$exDATE2}' AND wr.userID = '{$userID}'
                        GROUP BY wr.workout_Part 
                    )   
                
                UNION
                    (
                    SELECT part AS workout_Part ,0 AS volumn FROM workout_Part
                    )
                )AS t 
            GROUP BY workout_Part ;";


// $exsql = "SELECT wr.workout_Part,SUM(pr.Perform_count * pr.Perform_weight) AS volumn
// FROM set_list
// INNER JOIN workoutList_Record AS wr ON wr.workout_ID = set_list.workout_ID

// INNER JOIN perform_Record AS pr ON set_list.ID = pr.set_ID

// WHERE set_list.DATE between '{$exDATE1}' AND '{$exDATE2}' AND wr.userID = '{$userID}'
// GROUP BY wr.workout_Part ;";




$result = mysqli_query($con,$sql);  

$ex_result = mysqli_query($con,$exsql);  

$response = array();
$ex_respose = array();

while($row = mysqli_fetch_array($result))
{
    
    array_push($response,array('workout_Part'=>$row["workout_Part"],'volumn'=>$row["volumn"]));
}
while($ex_row = mysqli_fetch_array($ex_result))
{
    array_push($ex_respose,array('workout_Part'=>$ex_row["workout_Part"],'volumn'=>$ex_row["volumn"] ));


}

header('Content-Type: application/json; charset=utf8');
$json = json_encode(array("workout"=>$response ,"cal" =>$ex_respose ), JSON_PRETTY_PRINT+JSON_UNESCPED_UNICOCDE );
echo $json;

?>