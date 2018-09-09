import javafx.geometry.Pos;

import java.rmi.MarshalException;

public class FollowPathRobot2 {
    private RobotCommunication robotcomm;  // communication drivers
    /**
     * Create a robot connected to host "host" at port "port"
     * @param host normally http://127.0.0.1
     * @param port normally 50000
     */
    public  FollowPathRobot2(String host, int port)
    {
        robotcomm = new RobotCommunication(host, port);
    }


    public static void main(String[] args) throws Exception {
        ReadPath readPath = new ReadPath(args[0]);

        int pathsize = readPath.pathSize();
        Position[] path = readPath.getPath();
        System.out.println("position " + path[0].getX() + "," + path[0].getY());
        System.out.println("Creating Robot");
        FollowPathRobot2 robot = new FollowPathRobot2("http://127.0.0.1", 50000);
        robot.run(path, pathsize);
    }

    public void run(Position[] path, int pathSize) throws Exception{
        System.out.println("Creating response");
        LocalizationResponse lr = new LocalizationResponse();

        System.out.println("Creating request");
        DifferentialDriveRequest dr = new DifferentialDriveRequest();



        for(int i =0; i < pathSize; i = i+5){
            robotcomm.getResponse(lr);

            Position robotsPosition = new Position(lr.getPosition()[0], lr.getPosition()[1]);
            if (robotsPosition.getDistanceTo(path[i])< 0.3) {
                System.out.println("distance " +robotsPosition.getDistanceTo(path[i]));
                rotateRobot(i, robotsPosition, dr, lr, path);
            }
           else {
                dr.setAngularSpeed(0);
                dr.setLinearSpeed(0.5);
                robotcomm.putRequest(dr);
            }


        }

    }

    public void rotateRobot(int i, Position robotsPosition, DifferentialDriveRequest dr, LocalizationResponse lr, Position[] path)throws Exception{
        double bearingPoint = robotsPosition.getBearingTo(path[i]);
        System.out.println(lr.getHeadingAngle()- bearingPoint);

        if (lr.getHeadingAngle()-bearingPoint > 0){
            while(lr.getHeadingAngle()- bearingPoint >0.2){
                dr.setAngularSpeed(-0.3);
                dr.setLinearSpeed(0);
                robotcomm.putRequest(dr);
                robotcomm.getResponse(lr);
            }

        }
        else {
            while(bearingPoint - lr.getHeadingAngle() >0.2){
                dr.setAngularSpeed(0.3);
                dr.setLinearSpeed(0);
                robotcomm.putRequest(dr);
                robotcomm.getResponse(lr);
            }
        }

         /*if (bearingPoint + Math.PI> lr.getHeadingAngle() + Math.PI){
            if (lr.getHeadingAngle()-bearingPoint < Math.PI) {
                dr.setAngularSpeed(0.3);
                robotcomm.putRequest(dr);
            }
            else{
                dr.setAngularSpeed(-0.3);
                robotcomm.putRequest(dr);
            }
        }
        else if(bearingPoint - lr.getHeadingAngle() < Math.PI){
            if(bearingPoint - lr.getHeadingAngle() < Math.PI){
                dr.setAngularSpeed(-0.3);
                robotcomm.putRequest(dr);
            }
            else{
                dr.setAngularSpeed(0.3);
                robotcomm.putRequest(dr);
            }
        }*/


    }

}
