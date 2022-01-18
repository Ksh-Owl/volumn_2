import java.io.IOException;

import java.net.ServerSocket;

import java.net.Socket;

import java.security.Provider;
import java.util.Vector;

public class Server implements Runnable {
    //Server클래스: 소켓을 통한 접속서비스, 접속 클라이언트 관리

    Vector<Service> allV;//모든 사용자(대기실사용자 + 대화방 사용자)
    Vector<Service> waitV;//대기실 사용자
    Vector<Room> roomV;//개설된 대화방 Room-vs(Vector):대화방 사용자

    public Server(){
        allV = new Vector<>();
        waitV = new Vector<>();
        roomV = new Vector<>();

        //Thread t = new Thread(run메소드의 위치);  t.start();

        new Thread(this).start();
    }


    @Override
    public void run() {
        try {
            ServerSocket ss = new ServerSocket(5000);
            //현재 실행중인 ip+ 명시된 port -----> 소켓서비스

            System.out.println("Start Server :)");

            while (true)
            {
                Socket s = ss.accept();//클라이언트 접속 대기
                //s: 접속한 클라이언트의 소켓정보
                Service ser = new Service(s,this);

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public  static void main(String[] args){
        new Server();
    }
}
