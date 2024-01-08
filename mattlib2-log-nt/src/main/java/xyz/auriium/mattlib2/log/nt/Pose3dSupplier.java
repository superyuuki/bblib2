package xyz.auriium.mattlib2.log.nt;

import edu.wpi.first.math.geometry.*;
import edu.wpi.first.networktables.NetworkTableEntry;

import java.util.function.Supplier;

public class Pose3dSupplier implements Supplier<Pose3d> {

    final NetworkTableEntry entry;
    public Pose3dSupplier(NetworkTableEntry entry) {
       this.entry = entry;
    }

    @Override
    public Pose3d get() {
        double[] doubles = entry.getDoubleArray(new double[] {0,0,0,0,0,0});
        return new Pose3d(new Translation3d(doubles[0], doubles[1], doubles[2]), new Rotation3d(doubles[3], doubles[4], doubles[5]));
    }
}