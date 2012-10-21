package test;

import gates.GateImpl;

import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Date;

import messaging.AbstractMessage;
import messaging.CarArrivalMessage;
import simulation.TrafficGenerator;
import car.Car;


public class Test
{
    public static void main(String[] args) throws Exception
    {
        Date d  = new Date();
        Date e  = new Date();
        Car c = new Car(d, e);
        System.out.println(c.getTimeSent());

        TrafficGenerator t = new TrafficGenerator(100, "0,.1", InetAddress.getLocalHost(), 1234);

        //Traffic Generator
        Thread thread2 = new Thread(t);
        thread2.start();


        GateImpl g = new GateImpl(100, 100, null, InetAddress.getLocalHost(), 10000);
        Thread thread = new Thread(g);
        thread.start();

        CarArrivalMessage message = new CarArrivalMessage(new Date(), new Date());

        sendMessage(message, InetAddress.getLocalHost(), 1234);

    }

    public static String sendMessage(AbstractMessage message, InetAddress ip, int port)
    {
        try {
            Socket s = new Socket(ip, port);
            
            OutputStream o = s.getOutputStream();
            o.flush();

         

            AbstractMessage.encodeMessage(o, message);
            o.flush();
            s.close();
        } catch(Exception e) {
            System.out.println("Sadddnesss");
        }
        return null;
    }
}
