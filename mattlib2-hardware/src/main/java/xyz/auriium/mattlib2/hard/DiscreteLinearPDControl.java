package xyz.auriium.mattlib2.hard;

import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.Vector;
import edu.wpi.first.math.numbers.N1;
import xyz.auriium.mattlib2.log.components.impl.PIDConfig;

/**
 * A tuneable PD controller from Mattlib2
 */
public class DiscreteLinearPDControl implements ILinearPDControl {

    final PIDConfig pdComponent;
    final ILinearEncoder encoder; //nullable
    final IControlEffortReceiver<N1> actuator;
    final boolean hasEncoder;

    long lastTimestamp_seconds = System.currentTimeMillis() * 1000L;
    double lastError_meters = 0;
    Vector<N1> cachedVector = VecBuilder.fill(0);
    double lastPositionForLogging = 0;

    DiscreteLinearPDControl(PIDConfig pdComponent, ILinearEncoder encoder, IControlEffortReceiver<N1> actuator, boolean hasEncoder) {
        this.pdComponent = pdComponent;
        this.encoder = encoder;
        this.actuator = actuator;
        this.hasEncoder = hasEncoder;
    }

    public static DiscreteLinearPDControl withoutEncoder(PIDConfig pdComponent, IControlEffortReceiver<N1> actuator) {
        return new DiscreteLinearPDControl(pdComponent, null, actuator, false);
    }

    public static DiscreteLinearPDControl withEncoder(PIDConfig pdComponent, IControlEffortReceiver<N1> actuator, ILinearEncoder encoder) {
        return new DiscreteLinearPDControl(pdComponent, encoder, actuator, true);
    }

    @Override
    public void controlToLinearReference(double setpointMechanism_meters) {
        if (!hasEncoder) throw Exceptions.NO_BUILT_IN_ENCODER;

        double currentPosition_meters = encoder.linearPosition_mechanismMeters();
        controlToLinearReference(setpointMechanism_meters, currentPosition_meters);
    }

    @Override
    public void controlToLinearReference(double setpointMechanism_meters, double measurementMechanism_meters) {
        //PID LOOP
        long currentTimestamp_seconds = System.currentTimeMillis() * 1000L;

        long dt_seconds = currentTimestamp_seconds - lastTimestamp_seconds;
        double currentError_meters = setpointMechanism_meters - measurementMechanism_meters;
        double velocityError_metersDt = (currentError_meters - lastError_meters) / dt_seconds;

        double controlOutput = pdComponent.pConstant() * currentError_meters + pdComponent.dConstant() * velocityError_metersDt;
        cachedVector.set(0,0,controlOutput);
        actuator.handleControlEffort(cachedVector);

        //UPDATE INTERNAL STATE
        lastError_meters = currentError_meters;
        lastTimestamp_seconds = currentTimestamp_seconds;

        //UPDATE LOGGER
        lastPositionForLogging = controlOutput;
    }

    @Override
    public void init() {

    }

    @Override
    public void robotPeriodic() {

    }

    @Override
    public void logPeriodic() {
        pdComponent.reportError(lastError_meters);
        pdComponent.reportOutput(lastPositionForLogging);
    }

    @Override
    public void tunePeriodic() {
        if (pdComponent.hasUpdated()) immediateReset();
    }

    /**
     * FREAK THE FUCK OUT
     */
    void immediateReset() {
        lastError_meters = 0;
        cachedVector = VecBuilder.fill(0);
        lastPositionForLogging = 0;
        lastTimestamp_seconds = System.currentTimeMillis() * 1000L;
    }
}
