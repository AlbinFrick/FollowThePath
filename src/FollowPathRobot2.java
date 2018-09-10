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


        System.out.println("path size "+pathSize);
        for(int i =0; i < pathSize; i++){
            robotcomm.getResponse(lr);

            Position robotsPosition = new Position(lr.getPosition()[0], lr.getPosition()[1]);
            if (robotsPosition.getDistanceTo(path[i])< 0.3) {
                System.out.println("distance " +robotsPosition.getDistanceTo(path[i]));
                if(lr.getHeadingAngle() - robotsPosition.getBearingTo(path[1]) > 0.2 || robotsPosition.getBearingTo(path[1])- lr.getHeadingAngle() > 0.2){
                    dr.setLinearSpeed(0);
                    robotcomm.putRequest(dr);
                    rotateRobot(i, robotsPosition, dr, lr, path);
                    dr.setLinearSpeed(0.3);
                    robotcomm.putRequest(dr);
                }
                else{
                    dr.setLinearSpeed(0.3);
                    dr.setAngularSpeed(0);
                    robotcomm.putRequest(dr);
                }
            }
            else {
                dr.setLinearSpeed(0.3);
                dr.setAngularSpeed(0);
                robotcomm.putRequest(dr);
            }


        }

    }

    public void rotateRobot(int i, Position robotsPosition, DifferentialDriveRequest dr, LocalizationResponse lr, Position[] path)throws Exception{
        double bearingPoint = robotsPosition.getBearingTo(path[i]);
        System.out.println(lr.getHeadingAngle()- bearingPoint);

        if ((bearingPoint + Math.PI) > (lr.getHeadingAngle() + Math.PI)){
            if ((lr.getHeadingAngle()-bearingPoint )< Math.PI) {
                TurnLeft(lr,dr,bearingPoint);
            }
            else{
                TurnRight(lr,dr,bearingPoint);
            }
        }
        else if((bearingPoint - lr.getHeadingAngle()) < Math.PI){
            if((bearingPoint - lr.getHeadingAngle()) < Math.PI){
                TurnRight(lr,dr,bearingPoint);
            }
            else{
                TurnLeft(lr,dr,bearingPoint);
            }
        }

        /*if (lr.getHeadingAngle()-bearingPoint > 0){
            TurnRight(lr,dr,bearingPoint);

        }
        else {
            TurnLeft(lr,dr,bearingPoint);
        }*/
    }

    public void TurnRight(LocalizationResponse lr, DifferentialDriveRequest dr, double bearingPoint)throws Exception{
        while ((lr.getHeadingAngle()- bearingPoint) < 0){
            dr.setAngularSpeed(-0.3);
            robotcomm.putRequest(dr);
            robotcomm.getResponse(lr);
        }
        dr.setAngularSpeed(0);
        robotcomm.putRequest(dr);
    }

    public void TurnLeft(LocalizationResponse lr, DifferentialDriveRequest dr, double bearingPoint)throws Exception{
        while (bearingPoint > lr.getHeadingAngle()){
            dr.setAngularSpeed(0.3);
            robotcomm.putRequest(dr);
            robotcomm.getResponse(lr);
        }
        dr.setAngularSpeed(0);
        robotcomm.putRequest(dr);
    }


}
