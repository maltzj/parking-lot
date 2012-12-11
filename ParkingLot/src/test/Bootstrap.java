package test;
import simulation.TrafficGenerator;
import util.Config;

public class Bootstrap
{
    public static void main(String[] args) throws Exception
    {
        TrafficGenerator derp = new TrafficGenerator(Config.getSharedInstance().managers);
    }
}
