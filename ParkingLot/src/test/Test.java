package test;
import car.Car;
import simulation.TrafficGenerator;
import java.util.Date;

public class Test
{
    public static void main(String[] args)
    {
        Date d  = new Date();
        Date e  = new Date();
        Car c = new Car(d, e);
        System.out.println(c.getTimeSent());

        TrafficGenerator t = new TrafficGenerator(100, "0,.1");
        ArrayList<Gate> gates = new ArrayList<Gate>();

        t.run();

    }
}
