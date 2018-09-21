/**
 * Class: FollowPathRobot3
 * Description: This class creates a robot that can follow
 * a given path that consist of and array of positions.
 *
 * @author AlbinF 20/9-18
 * @author JonathanH 20/9-18
 */

public class FollowPathRobot3 {
    private RobotCommunication robotcomm;  // communication drivers
    private Position robotPos;
    private DifferentialDriveRequest dr;
    private LocalizationResponse lr;
    private double bearingPoint;

    //parameters
    private double turnspeed = 1.5;
    private double angleprecision = 0.18;
    private double lookAheadDistance;
    private double robotSpeed;
    private double driftSpeed;


    /**
     * Constructor: FollowPathRobot3
     * Description: Create a robot connected to host "host" at port "port"
     * @param host normally http://127.0.0.1
     * @param port normally 50000
     */
    public  FollowPathRobot3(String host, int port)
    {
        robotcomm = new RobotCommunication(host, port);
    }

    /**
     * Method: Run
     * Description: This method uses all the method in the Class and makes
     * the robot follow the given path.
     * @param path - Position[]
     * @throws Exception
     */
    public void Run(Position[] path) throws Exception {
        System.out.println("Creating response");
        lr = new LocalizationResponse();

        System.out.println("Creating request");
        dr = new DifferentialDriveRequest();
        robotcomm.getResponse(lr);
        robotPos = new Position(lr.getPosition()[0], lr.getPosition()[1]);

        long startTime = System.nanoTime();
        for (int i = 0; i < path.length; i++) {
            if (CloseToObject()){
                driftSpeed = 0.4;
                robotSpeed = 0.6;
                lookAheadDistance = 0.3;
            }
            else{
                driftSpeed = 2;
                robotSpeed = 5;
                lookAheadDistance = 1;
            }
            robotcomm.putRequest(dr);
            robotPos = new Position(lr.getPosition()[0], lr.getPosition()[1]);
            if(Math.abs(robotPos.getDistanceTo(path[i])) > (lookAheadDistance-0.2)){
                //Visar vilka steg roboten väljer att köra mot
                //System.out.println("Steg: " + i + " av " + path.length);
                RotateRobot(path[i]);
                DriveRobot(path[i]);
            }

        }
        long stopTime = System.nanoTime();
        dr.setLinearSpeed(0);
        robotcomm.putRequest(dr);

        System.out.println("Klar. Tid: " + ((stopTime-startTime)/1000000000) + " sekunder");
    }

    /**
     * Method: RotateRobot
     * Description: This is a void method that decides if to turn right or
     * left depending of the next position to go to.
     * @param rotateToPoint - position.
     * @throws Exception
     */
    private void RotateRobot(Position rotateToPoint) throws Exception {
        robotcomm.getResponse(lr);
        bearingPoint = robotPos.getBearingTo(rotateToPoint);
        if (lr.getHeadingAngle() > bearingPoint){
            if((lr.getHeadingAngle()-Math.PI) > bearingPoint){
                Turn(false);
            }
            else {
                Turn(true);
            }
        }
        else {
            if((lr.getHeadingAngle()+Math.PI) < bearingPoint){
                Turn(true);
            }
            else{
                Turn(false);
            }
        }
    }

    /**
     * Method: Turn
     * Description: This is a void method that turns the robot
     * left or right dependent on the input parameter.
     * The robot will rotate until it's pointing roughly at the next point.
     * How precis the robot will be pointed is dependent on the angleprecision.
     * true = right
     * false = left
     * @param turn
     * @throws Exception
     */
    private void Turn(Boolean turn) throws Exception {
        if (turn){
            while (Math.abs(bearingPoint-lr.getHeadingAngle()) > angleprecision){
                dr.setAngularSpeed(-turnspeed);
                robotcomm.putRequest(dr);
                robotcomm.getResponse(lr);
            }
            dr.setAngularSpeed(0);
            robotcomm.putRequest(dr);
        }
        else{
            while (Math.abs(bearingPoint-lr.getHeadingAngle()) > angleprecision){
                dr.setAngularSpeed(turnspeed);
                robotcomm.putRequest(dr);
                robotcomm.getResponse(lr);
            }
            dr.setAngularSpeed(0);
            robotcomm.putRequest(dr);
        }

    }

    /**
     * Method: DriveRobot
     * Description: This is a void method that takes in an parameter
     * of a position and drives the robot forward until the distance
     * to the given position is less then the lookAheadDistance.
     * @param driveToPoint - position
     * @throws Exception
     */
    private void DriveRobot(Position driveToPoint) throws Exception{
        robotcomm.getResponse(lr);
        while (Math.abs(robotPos.getDistanceTo(driveToPoint)) > lookAheadDistance){
            dr.setLinearSpeed(robotSpeed);
            robotcomm.putRequest(dr);
            robotcomm.getResponse(lr);
            robotPos = new Position(lr.getPosition()[0], lr.getPosition()[1]);
        }
        dr.setLinearSpeed(driftSpeed);
        robotcomm.putRequest(dr);
    }

    /**
     * Method: CloseTObject
     * Description: This is a boolean method that
     * determines if there is an object close to the robot.
     * @return - boolean
     * @throws Exception
     */
    private Boolean CloseToObject() throws Exception {
        LaserEchoesResponse ler = new LaserEchoesResponse();
        robotcomm.getResponse(ler);
        double[] echos = ler.getEchoes();
        for (int i = 0; i < echos.length; i++){
            if(echos[i]< 0.4){
                return true;
            }
        }
        return false;
    }
}