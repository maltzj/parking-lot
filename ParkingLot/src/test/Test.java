package test;
import car.Car;
import gates.GateImpl;
import gates.Gate;
import simulation.TrafficGenerator;
import java.util.*;
import messaging.*;
import java.net.*;
import java.io.*;

public class Test
{
    public static void main(String[] args) throws Exception
    {
        Date d  = new Date();
        Date e  = new Date();
        Car c = new Car(d, e);
        System.out.println(c.getTimeSent());

        TrafficGenerator t = new TrafficGenerator(100, "0,.1");
        ArrayList<Gate> gates = new ArrayList<Gate>();


        GateImpl g = new GateImpl(100, 100, null, 10000);
        Thread thread = new Thread(g);
        gates.add(g);
        thread.start();

        CarArrivalMessage message = new CarArrivalMessage(new Date(), new Date());

    }

    public static String sendMessage(AbstractMessage message, InetAddress ip, int port)
    {
        try {
            Socket s = new Socket(ip, port);
            
            ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());

            out.writeObject(message);
            out.flush();
        } catch(Exception e) {
            System.out.println("Sadddnesss");
        }
        return null;
    }
}
