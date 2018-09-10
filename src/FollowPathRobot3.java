import javafx.geometry.Pos;

public class FollowPathRobot3 {
    private RobotCommunication robotcomm;  // communication drivers
    private Position robotPos;
    private DifferentialDriveRequest dr;
    private LocalizationResponse lr;
    private Position[] path;
    double bearingPoint;
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
            robotPos = new Position(lr.getPosition()[0], lr.getPosition()[1]);
            if(Math.abs(robotPos.getDistanceTo(path[i])) > 0.3){
                System.out.println("Vi är på steg: " + i + " av " + pathSize);
                rotateRobot(path[i]);
                driveRobot(path[i]);
            }

        }
        long stopTime = System.nanoTime();
        dr.setLinearSpeed(0);
        robotcomm.putRequest(dr);

        System.out.println("Klar. Tid: " + ((stopTime-startTime)/10000000));
    }

    private void rotateRobot(Position rotateToPoint) throws Exception {
        robotcomm.getResponse(lr);
        bearingPoint = robotPos.getBearingTo(rotateToPoint);
        double angleprecision = 0.3;
        double turnspeed = 1.5;
        if (lr.getHeadingAngle() > bearingPoint){
            if((lr.getHeadingAngle()-Math.PI) > bearingPoint){
                while (Math.abs(lr.getHeadingAngle()-bearingPoint) > angleprecision){
                    dr.setAngularSpeed(turnspeed);
                    robotcomm.putRequest(dr);
                    robotcomm.getResponse(lr);
                }
            }
            else {
                while (Math.abs(lr.getHeadingAngle() - bearingPoint) > angleprecision) {
                    dr.setAngularSpeed(-turnspeed);
                    robotcomm.putRequest(dr);
                    robotcomm.getResponse(lr);
                }
            }
            dr.setAngularSpeed(0);
            robotcomm.putRequest(dr);
        }
        else {
            if((lr.getHeadingAngle()+Math.PI) < bearingPoint){
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
        double turnspeed = 1;
        double angleprecision = 0.23;
        if (turn){
            while (Math.abs(bearingPoint-lr.getHeadingAngle()) > angleprecision){
                dr.setAngularSpeed(turnspeed);
                robotcomm.putRequest(dr);
                robotcomm.getResponse(lr);
            }
        }
        else{
            turnspeed = -1;
            while (Math.abs(bearingPoint-lr.getHeadingAngle()) > angleprecision){
                dr.setAngularSpeed(turnspeed);
                robotcomm.putRequest(dr);
                robotcomm.getResponse(lr);
            }
        }

    }

    private void driveRobot(Position driveToPoint) throws Exception{
        robotcomm.getResponse(lr);
        //byt till bättre namn här!!!! sen!
        double lookAheadDistance = 0.3;
        double robotSpeed = 0.93;
        while (Math.abs(robotPos.getDistanceTo(driveToPoint)) > lookAheadDistance){
            dr.setLinearSpeed(robotSpeed);
            robotcomm.putRequest(dr);
            robotcomm.getResponse(lr);
            robotPos = new Position(lr.getPosition()[0], lr.getPosition()[1]);
        }
        dr.setLinearSpeed(0.57);
        robotcomm.putRequest(dr);

    }

}
