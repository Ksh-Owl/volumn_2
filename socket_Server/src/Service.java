import java.io.BufferedReader;

import java.io.IOException;

import java.io.InputStreamReader;

import java.io.OutputStream;

import java.net.Socket;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Vector;

public class Service extends Thread{
    //Service == 접속 클라이언트 한명!!



    Room myRoom;//클라이언트가 입장한 대화방



    //소켓관련 입출력서비스

    BufferedReader in;

    OutputStream out;



    Vector<Service> allV;//모든 사용자(대기실사용자 + 대화방사용자)

    Vector<Service> waitV;//대기실 사용자

    Vector<Room> roomV;//개설된 대화방 Room-vs(Vector) : 대화방사용자



    Socket s;



    String nickName;



    public Service(Socket s, Server server) {

        allV=server.allV;

        waitV=server.waitV;

        roomV=server.roomV;



        this.s = s;



        try {

            in = new BufferedReader(new InputStreamReader(s.getInputStream()));

            out = s.getOutputStream();



            start();

        } catch (IOException e) {

            e.printStackTrace();

        }
    }//생성자
    @Override

    public void run() {

        try {



            while(true){

                String msg = in.readLine();//클라이언트의 모든 메시지를 받기



                if(msg == null) return; //비정상적인 종료



                if(msg.trim().length() > 0){

                    System.out.println("from Client: "+ msg +":"+

                            s.getInetAddress().getHostAddress());

                    //서버에서 상황을 모니터!!



                    String msgs[]=msg.split("\\|");

                    String protocol = msgs[0];



                    switch(protocol){

                        case "100": //대기실 접속

                            boolean allV_Check =false;
                            boolean waitV_Check =false;
                            nickName=msgs[1];



                            //방또는 대기실에 같은 닉네임 있으면 같은 사람이다
                            for (int i = 0; i <allV.size(); i++){
                                Service Object_ser =  allV.get(i);

                                if(Object_ser.nickName.equals(nickName) )
                                {
                                    System.out.println("전체사용자 중복닉네임 발견 :"+this);

                                    allV.set(i,this);
                                    System.out.println("전체사용자 중복닉네임 위치에 현재 사용자 변경:"+this);

                                    allV_Check = true;
                                }

                            }
                            for (int i = 0; i <waitV.size(); i++){
                                Service Object_ser =  waitV.get(i);

                                if(Object_ser.nickName.equals(nickName) )
                                {
                                    System.out.println("대기실사용자 중복닉네임 발견 :"+this);

                                    waitV.set(i,this);
                                    System.out.println("대기실사용자 중복닉네임 위치에 현재 사용자 변경:"+this);

                                    waitV_Check = true;
                                }

                            }
                            if(!allV_Check  && !waitV_Check){
                                allV.add(this);//전체사용자에 등록

                                waitV.add(this);//대기실사용자에 등록

                                System.out.println(""+this);

                            }


                            break;



                        case "150": //대화명 입력




                            //최초 대화명 입력했을때 대기실의 정보를 출력

                            messageWait("160|"+ getRoomInfo());

                            messageWait("180|"+ getWaitInwon());



                            break;

                        case "160": //방만들기 (대화방 입장)

                            myRoom = new Room();

                            myRoom.title =msgs[1];//방제목

                            myRoom.count = 1;

                            myRoom.boss = nickName;



                            roomV.add(myRoom);



                            //대기실----> 대화방 이동!!

                            //waitV.remove(this);

                            myRoom.userV.add(this);



                            //messageRoom("200|"+nickName);//방인원에게 입장 알림
                            messageRoom("200|"+myRoom.title +"|"+nickName);//방인원에게 입장 알림



                            //대기실 사용자들에게 방정보를 출력

                            //예) 대화방명:JavaLove

                            //-----> roomInfo(JList) :  JavaLove--1

                            messageWait("160|"+ getRoomInfo());

                            messageWait("180|"+ getWaitInwon());

                            break;



                        case "170": //(대기실에서) 대화방 인원정보

                            messageTo("170|"+getRoomInwon(msgs[1]));

                            break;



                        case "175": //(대화방에서) 대화방 인원정보

                            messageRoom("175|"+getRoomInwon());

                            break;



                        case "200": //방들어가기 (대화방 입장) ----> msgs[] = {"200","자바방"}

                            for(int i=0; i<roomV.size(); i++){//방이름 찾기!!

                                Room r = roomV.get(i);

                                if(r.title.equals(msgs[1])){//일치하는 방 찾음!!

                                    myRoom = r;




                                }else //일치하는 방 없음
                                {
                                   //DB에서 방삭제 추가 필요
                                }

                            }//for



                            //대기실----> 대화방 이동!!
                            //대기실 개념 삭제

                            //waitV.remove(this);
                            //System.out.println(""+this);

                            //방에 같은 이름 있으면 거기에 넣기
                            boolean userV_Check = false;

                            if(myRoom != null && myRoom.userV.size() >0)//2021년 11월 21일 수정
                            {
                                for (int i = 0; i <myRoom.userV.size(); i++){
                                    Service Object_user =  myRoom.userV.get(i);

                                    if(Object_user.nickName.equals(nickName) )
                                    {
                                        System.out.println( myRoom.title+" 방에서 중복닉네임 발견 :"+nickName);

                                        myRoom.userV.set(i,this);
                                        System.out.println(myRoom.title+" 방에서 중복닉네임 위치에 현재 사용자 변경:"+nickName);

                                        userV_Check = true;
                                    }

                                }

                                if(!userV_Check)
                                {

                                    myRoom.userV.add(this);
                                    myRoom.count++;//인원수 1증가
                                    messageRoom("200|"+myRoom.title +"|"+nickName);//방인원에게 입장 알림

                                }




                                //들어갈 방의 title전달

                                messageTo("202|"+ myRoom.title);



                                messageWait("160|"+ getRoomInfo());

                                messageWait("180|"+ getWaitInwon());
                            }






                            break;

                        case "301": //이미지 메시지
                            // 현재 날짜 구하기 (시스템 시계, 시스템 타임존)
                            Date now2 = new Date();
                            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd/HH:mm:ss"); // 원하는 포맷의 객체 생성

                            String formatedNow2 = sdf2.format(now2);

                            if(myRoom.title == null || nickName ==null)
                            {
                                return;
                            }

                            messageRoom("301|"+myRoom.title +"|" +formatedNow2 + "|["+nickName +"]▶ "+msgs[1]+"|"+myRoom.userV.size()+"|"+msgs[2] );

                            //클라이언트에게 메시지 보내기

                            break;

                        case "300": //메시지
                            // 현재 날짜 구하기 (시스템 시계, 시스템 타임존)
                            Date now = new Date();
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/HH:mm:ss"); // 원하는 포맷의 객체 생성

                            String formatedNow = sdf.format(now);

                            if(myRoom.title == null || nickName ==null)
                            {
                                return;
                            }

                            messageRoom("300|"+myRoom.title +"|" +formatedNow + "|["+nickName +"]▶ "+msgs[1]+"|"+myRoom.userV.size() );

                            //클라이언트에게 메시지 보내기

                            break;
                        case "303": //메시지 읽음
                            String Room_name = msgs[1];
                            String user_id = msgs[2];

                            messageRoom("303|"+Room_name+"|" +user_id );

                            break;


                        case "400": //대화방 퇴장

                            String title = msgs[1];
                            nickName=msgs[2];

//                            //방이름 찾기
//                            for (int i = 0; i<roomV.size(); i++)
//                            {
//                                Room r = roomV.get(i);
//
//                                if(r.title.equals(title))
//                                {
//                                    myRoom = r;
//
//
//
//
//
//                                }else
//                                {
//                                    //나가기를 했는데 일치하는 방이 없음
//                                    return;
//                                }
//                            }
                            myRoom.count--;//인원수 감소

                            myRoom.userV.remove(this);

                            messageRoom("400|"+ title + "|"+nickName+"|");//방인원들에게 퇴장 알림!!


                            //대화방 퇴장후 방인원 다시출력

                            messageRoom("175|"+getRoomInwon());

                            //messageRoom("인원|"+myRoom.userV.size());


                            //대기실에 방정보 다시출력

                            messageWait("160|"+ getRoomInfo());



                            break;



                    }//서버 switch

                }//if

            }//while

        }catch (IOException e) {

            System.out.println("★");

            e.printStackTrace();

        }

    }//run

    public String getRoomInfo(){

        String str="";

        for(int i=0; i<roomV.size(); i++){

            //"자바방--1,오라클방--1,JDBC방--1"

            Room r= roomV.get(i);

            str += r.title+"--"+r.userV.size();

            if(i<roomV.size()-1)str += ",";

        }

        return str;

    }//getRoomInfo



    public String getRoomInwon(){//같은방의 인원정보

        String str="";

        for(int i=0; i<myRoom.userV.size(); i++){

            //"길동,라임,주원"

            Service ser= myRoom.userV.get(i);

            str += ser.nickName;

            if(i<myRoom.userV.size()-1)str += ",";

        }

        return str;

    }//getRoomInwon



    public String getRoomInwon(String title){//방제목 클릭시 방의 인원정보

        String str="";



        for(int i=0; i<roomV.size(); i++){

            //"길동,라임,주원"

            Room room = roomV.get(i);

            if(room.title.equals(title)){

                for(int j=0; j<room.userV.size(); j++){

                    Service ser= room.userV.get(j);

                    str += ser.nickName;

                    if(j<room.userV.size()-1)str += ",";

                }

                break;

            }

        }

        return str;

    }//getRoomInwon



    public String getWaitInwon(){

        String str="";

        for(int i=0; i<waitV.size(); i++){

            //"길동,라임,주원"

            Service ser= waitV.get(i);

            str += ser.nickName;

            if(i<waitV.size()-1)str += ",";

        }

        return str;

    }//getWaitInwon



    public void messageAll(String msg){//전체사용자

        //접속된 모든 클라이언트(대기실+대화방)에게 메시지 전달

        for(int i=0; i<allV.size(); i++){//벡터 인덱스

            Service service = allV.get(i); //각각의 클라이언트 얻어오기

            try {

                service.messageTo(msg);
                System.out.println(""+msg);

            } catch (IOException e) {

                //에러발생 ---> 클라이언트 접속 끊음!!

                allV.remove(i--); //접속 끊긴 클라이언트를 벡터에서 삭제!!

                System.out.println("클라이언트 접속 끊음!!");

            }

        }

    }//messageAll



    public void messageWait(String msg){//대기실 사용자

        for(int i=0; i<waitV.size(); i++){//벡터 인덱스

            Service service = waitV.get(i); //각각의 클라이언트 얻어오기

            try {

                service.messageTo(msg);
                System.out.println(""+msg);

            } catch (IOException e) {

                //에러발생 ---> 클라이언트 접속 끊음!!
                //접속 끊겨도 벡터삭제 안함 이유는
                //닉네임으로 다시 연결하여 채팅 정보 유지 할 수 있도록

               // waitV.remove(i--); //접속 끊긴 클라이언트를 벡터에서 삭제!!

              //  System.out.println("클라이언트 접속 끊음!!");

            }

        }

    }//messageWait



    public void messageRoom(String msg){//대화방사용자

        for(int i=0; i< myRoom.userV.size(); i++){//벡터 인덱스


            Service service = myRoom.userV.get(i); //각각의 클라이언트 얻어오기

            try {
                //마지막
                for (int j =0; j<myRoom.userV.size(); j++)
                {
                    if( i != j &&  myRoom.userV.get(i) == myRoom.userV.get(j))
                    {
                        myRoom.userV.remove(j);
                    }
                }
                service.messageTo(msg);
                System.out.println(""+msg);



            } catch (IOException e) {

                //에러발생 ---> 클라이언트 접속 끊음!!
                //접속 끊겨도 벡터삭제 안함 이유는
                //닉네임으로 다시 연결하여 채팅 정보 유지 할 수 있도록
                //myRoom.userV.remove(i--); //접속 끊긴 클라이언트를 벡터에서 삭제!!

                //System.out.println("클라이언트 접속 끊음!!");

            }

        }

    }//messageAll



    public void messageTo(String msg) throws IOException{

        //특정 클라이언트에게 메시지 전달 (실제 서버--->클라이언트 메시지 전달)
        System.out.println(""+msg);
        out.write(  (msg+"\n").getBytes()   );

    }
}
