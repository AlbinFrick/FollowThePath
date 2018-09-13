import javafx.geometry.Pos;

public class FollowPathRobot3 {
    private RobotCommunication robotcomm;  // communication drivers
    private Position robotPos;
    private DifferentialDriveRequest dr;
    private LocalizationResponse lr;
    private Position[] path;
    private double bearingPoint;


    //parameters
    private double turnspeed = 1.5;
    private double angleprecision = 0.18;
    private double lookAheadDistance;
    private double robotSpeed;
    private double driftSpeed;
    /**
     * Create a robot connected to host "host" at port "port"
     * @param host normally http://127.0.0.1
     * @param port normally 50000
     */
    public  FollowPathRobot3(String host, int port)
    {
        robotcomm = new RobotCommunication(host, port);
    }

    public void run(Position[] path, int pathSize) throws Exception {
        this.path = path;
        System.out.println("Creating response");
        lr = new LocalizationResponse();

        System.out.println("Creating request");
        dr = new DifferentialDriveRequest();
        robotcomm.getResponse(lr);
        robotPos = new Position(lr.getPosition()[0], lr.getPosition()[1]);

        long startTime = System.nanoTime();
        for (int i = 0; i < pathSize; i++) {
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
              //  System.out.println("Vi är på steg: " + i + " av " + pathSize);
                RotateRobot(path[i]);
                DriveRobot(path[i]);
            }

        }
        long stopTime = System.nanoTime();
        dr.setLinearSpeed(0);
        robotcomm.putRequest(dr);

        System.out.println("Klar. Tid: " + ((stopTime-startTime)/10000000));
    }

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
            dr.setAngularSpeed(0);
            robotcomm.putRequest(dr);
        }
        else {
            if((lr.getHeadingAngle()+Math.PI) < bearingPoint){
                Turn(true);
            }
            else{
                Turn(false);
            }
            dr.setAngularSpeed(0);
            robotcomm.putRequest(dr);
        }
    }

    /**
     * turn = true höger
     * turn = false vänster
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
        }
        else{
            while (Math.abs(bearingPoint-lr.getHeadingAngle()) > angleprecision){
                dr.setAngularSpeed(turnspeed);
                robotcomm.putRequest(dr);
                robotcomm.getResponse(lr);
            }
        }

    }

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

/*Jonathans älskebarn
//om roboten skulle köra in i något
            if ((distansToPoint+0.3) > Math.abs(robotPos.getDistanceTo(driveToPoint))){
                dr.setLinearSpeed(0);
                robotcomm.putRequest(dr);
                return;dS
            }
 */
