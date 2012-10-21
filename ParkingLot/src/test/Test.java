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

        //Gate
        GateImpl g = new GateImpl(100, 100, null, 10000);
        Thread thread = new Thread(g);
        thread.start();

        //Traffic Generator
        Thread thread2 = new Thread(t);
        thread2.start();

        CarArrivalMessage message = new CarArrivalMessage(new Date(), new Date());

        sendMessage(message, InetAddress.getLocalHost(), 10000);
    }

    public static String sendMessage(AbstractMessage message, InetAddress ip, int port)
    {
        try {
            Socket s = new Socket(ip, port);
            
            OutputStream o = s.getOutputStream();

         

            AbstractMessage.encodeMessage(o, message);
        } catch(Exception e) {
            System.out.println("Sadddnesss");
        }
        return null;
    }
}
