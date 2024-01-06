package xyz.auriium.mattlib2.rev;

import com.revrobotics.RelativeEncoder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import xyz.auriium.mattlib2.log.components.impl.CANNetworkedConfig;
import xyz.auriium.mattlib2.log.components.impl.MotorNetworkedConfig;

import static org.mockito.Mockito.mock;

public class SparkMaxTest {



    @Test
    public void wrappingOfNormalNumbersShouldWork() {
        Mockito.withSettings().serializable();

        MotorNetworkedConfig motorComponent = mock(MotorNetworkedConfig.class);
        CANNetworkedConfig canComponent = mock(CANNetworkedConfig.class);
        RelativeEncoder relativeEncoder = Mockito.mock(RelativeEncoder.class);
        Mockito.when(relativeEncoder.getPosition()).thenReturn(0.35);
        Mockito.when(motorComponent.encoderToMechanismCoefficient()).thenReturn(1d); //we are testing normal

        BaseSparkMotor mx = new BaseSparkMotor(null, canComponent, motorComponent, relativeEncoder);

        Assertions.assertEquals(0.35, mx.angularPosition_encoderRotations());
        Assertions.assertEquals(0.35, mx.angularPosition_mechanismRotations());
        Assertions.assertEquals(0.35, mx.angularPosition_normalizedEncoderRotations()); //should be the same
    }

    @Test
    public void wrappingOfLargeNumbersShouldWork() {

        MotorNetworkedConfig motorComponent = mock(MotorNetworkedConfig.class);
        CANNetworkedConfig canComponent = mock(CANNetworkedConfig.class);
        RelativeEncoder relativeEncoder = Mockito.mock(RelativeEncoder.class);
        Mockito.when(relativeEncoder.getPosition()).thenReturn(1.35);
        Mockito.when(motorComponent.encoderToMechanismCoefficient()).thenReturn(1d); //we are testing normal

        BaseSparkMotor mx = new BaseSparkMotor(null, canComponent, motorComponent, relativeEncoder);

        Assertions.assertEquals(1.35, mx.angularPosition_encoderRotations(), 0.00001);
        Assertions.assertEquals(1.35, mx.angularPosition_mechanismRotations(), 0.00001);
        Assertions.assertEquals(0.35, mx.angularPosition_normalizedEncoderRotations(), 0.00001); //should be the same
        Assertions.assertEquals(0.35, mx.angularPosition_normalizedMechanismRotations(), 0.00001); //should be the same

    }

    @Test
    public void wrappingOfNegativeLargeNumbersShouldWork() {

        MotorNetworkedConfig motorComponent = mock(MotorNetworkedConfig.class);
        CANNetworkedConfig canComponent = mock(CANNetworkedConfig.class);
        RelativeEncoder relativeEncoder = Mockito.mock(RelativeEncoder.class);
        Mockito.when(relativeEncoder.getPosition()).thenReturn(-4.76);
        Mockito.when(motorComponent.encoderToMechanismCoefficient()).thenReturn(1d); //we are testing normal

        BaseSparkMotor mx = new BaseSparkMotor(null, canComponent, motorComponent, relativeEncoder);

        Assertions.assertEquals(-4.76, mx.angularPosition_encoderRotations(), 0.00001);
        Assertions.assertEquals(-4.76, mx.angularPosition_mechanismRotations(), 0.00001);
        Assertions.assertEquals(0.24, mx.angularPosition_normalizedEncoderRotations(), 0.00001); //should be the same
        Assertions.assertEquals(0.24, mx.angularPosition_normalizedMechanismRotations(), 0.00001); //should be the same

    }

    @Test
    public void wrappingOfMechanismNumbersShouldWork() {

        MotorNetworkedConfig motorComponent = mock(MotorNetworkedConfig.class);
        CANNetworkedConfig canComponent = mock(CANNetworkedConfig.class);
        RelativeEncoder relativeEncoder = Mockito.mock(RelativeEncoder.class);
        Mockito.when(relativeEncoder.getPosition()).thenReturn(-4.76);
        Mockito.when(motorComponent.encoderToMechanismCoefficient()).thenReturn(3d); //we are testing normal

        BaseSparkMotor mx = new BaseSparkMotor(null, canComponent, motorComponent, relativeEncoder);

        Assertions.assertEquals(-4.76, mx.angularPosition_encoderRotations(), 0.00001);
        Assertions.assertEquals(-4.76 * 3d, mx.angularPosition_mechanismRotations(), 0.00001);
        Assertions.assertEquals(0.24, mx.angularPosition_normalizedEncoderRotations(), 0.00001); //should be the same
        Assertions.assertEquals(0.24 * 3d, mx.angularPosition_normalizedMechanismRotations(), 0.00001); //should be the same

    }

}