import javafx.geometry.Pos;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class FollowPathRobot {

    private RobotCommunication robotcomm;  // communication drivers
    /**
     * Create a robot connected to host "host" at port "port"
     * @param host normally http://127.0.0.1
     * @param port normally 50000
     */
    public  FollowPathRobot(String host, int port)
    {
        robotcomm = new RobotCommunication(host, port);
    }


    public static void main(String[] args) throws Exception {
       ReadPath readPath = new ReadPath(args[0]);

       int pathsize = readPath.pathSize();
       Position[] path = readPath.getPath();
        System.out.println("position " + path[0].getX() + "," + path[0].getY());
       System.out.println("Creating Robot");
       FollowPathRobot robot = new FollowPathRobot("http://127.0.0.1", 50000);
       robot.run(path);
       // robot.run2(path, pathsize);

    }

    private  void run2(Position[] path, int pathSize) throws Exception{
        System.out.println("Creating response");
        LocalizationResponse lr = new LocalizationResponse();

        System.out.println("Creating request");
        DifferentialDriveRequest dr = new DifferentialDriveRequest();

        robotcomm.getResponse(lr);

        Position robotsPosition = new Position(lr.getPosition()[0], lr.getPosition()[1]);

        for(int i =10; i < pathSize; i = i +10){
            dr.setLinearSpeed(0);
            robotcomm.putRequest(dr);
            Position nextPoint = path[i];
            double nextAngle  = robotsPosition.getBearingTo(nextPoint);
            if (nextAngle > 0){
                while(nextAngle > lr.getHeadingAngle()){
                    dr.setAngularSpeed(0.3);
                    dr.setLinearSpeed(0.1);
                    robotcomm.putRequest(dr);
                    robotcomm.getResponse(lr);
                    robotsPosition = new Position(lr.getPosition()[0], lr.getPosition()[1]);
                    nextAngle = robotsPosition.getBearingTo(nextPoint);
                    System.out.println(lr.getHeadingAngle());
                }
            }
            else{
                while(nextAngle < lr.getHeadingAngle()){
                    dr.setAngularSpeed(-0.3);
                    dr.setLinearSpeed(0.1);
                    robotcomm.putRequest(dr);
                    robotcomm.getResponse(lr);
                    robotsPosition = new Position(lr.getPosition()[0], lr.getPosition()[1]);
                    nextAngle = robotsPosition.getBearingTo(nextPoint);
                }
            }
            dr.setAngularSpeed(0);
            dr.setLinearSpeed(0);
            robotcomm.putRequest(dr);
        }
    }

    private void run(Position[] path) throws Exception{
        System.out.println("Creating response");
        LocalizationResponse lr = new LocalizationResponse();

        System.out.println("Creating request");
        DifferentialDriveRequest dr = new DifferentialDriveRequest();

        robotcomm.getResponse(lr);

        double[] pos = lr.getPosition();
        Position roboPos = new Position(pos[0], pos[1]);
        Position testpos = new Position(1.736, 0.44);
        //Position testpos = path[100];

        double angleToPoint = roboPos.getBearingTo(testpos);
        System.out.println(pos[0]);

        while(angleToPoint > lr.getHeadingAngle()){
            robotcomm.putRequest(dr);
            dr.setAngularSpeed(0.3);
            dr.setLinearSpeed(0);

            //Thread.sleep(1000);
            robotcomm.getResponse(lr);
            roboPos =  new Position(lr.getPosition()[0], lr.getPosition()[1]);
            angleToPoint = roboPos.getBearingTo(testpos);
            System.out.println(lr.getPosition()[0] + " " + lr.getPosition()[1]);
            System.out.println(lr.getHeadingAngle());

        }
        dr.setAngularSpeed(0);
        dr.setLinearSpeed(0.5);
        robotcomm.putRequest(dr);

        Thread.sleep(6000);

        /*while(roboPos != testpos){
            robotcomm.getResponse(lr);
            roboPos =  new Position(lr.getPosition()[0], lr.getPosition()[1]);
            angleToPoint = roboPos.getBearingTo(testpos);
            System.out.println(lr.getPosition()[0] + " " + lr.getPosition()[1]);
            System.out.println(lr.getHeadingAngle());
        }*/
        dr.setAngularSpeed(0);
        dr.setLinearSpeed(0);
        robotcomm.putRequest(dr);



    }
}
