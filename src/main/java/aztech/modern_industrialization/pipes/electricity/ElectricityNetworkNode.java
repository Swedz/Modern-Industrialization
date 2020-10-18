package aztech.modern_industrialization.pipes.electricity;

import static aztech.modern_industrialization.pipes.api.PipeEndpointType.*;

import alexiil.mc.lib.attributes.SearchOption;
import alexiil.mc.lib.attributes.SearchOptions;
import aztech.modern_industrialization.api.energy.CableTier;
import aztech.modern_industrialization.api.energy.EnergyAttributes;
import aztech.modern_industrialization.api.energy.EnergyExtractable;
import aztech.modern_industrialization.api.energy.EnergyInsertable;
import aztech.modern_industrialization.pipes.api.PipeEndpointType;
import aztech.modern_industrialization.pipes.api.PipeNetworkNode;
import aztech.modern_industrialization.util.NbtHelper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class ElectricityNetworkNode extends PipeNetworkNode {
    private List<Direction> connections = new ArrayList<>();
    long eu = 0;

    public void appendAttributes(World world, BlockPos pos, List<EnergyInsertable> insertables, List<EnergyExtractable> extractables) {
        for (Direction direction : connections) {
            SearchOption option = SearchOptions.inDirection(direction);
            // TODO: get() instead of getFirst() ?
            EnergyInsertable insertable = EnergyAttributes.INSERTABLE.getFirstOrNull(world, pos.offset(direction), option);
            if (insertable != null)
                insertables.add(insertable);
            EnergyExtractable extractable = EnergyAttributes.EXTRACTABLE.getFirstOrNull(world, pos.offset(direction), option);
            if (extractable != null)
                extractables.add(extractable);
        }
    }

    @Override
    public void updateConnections(World world, BlockPos pos) {
        // We don't connect by default, so we just have to remove connections that have
        // become unavailable
        for (int i = 0; i < connections.size();) {
            if (canConnect(world, pos, connections.get(i))) {
                i++;
            } else {
                connections.remove(i);
            }
        }
    }

    @Override
    public PipeEndpointType[] getConnections(BlockPos pos) {
        PipeEndpointType[] connections = new PipeEndpointType[6];
        for (Direction direction : network.manager.getNodeLinks(pos)) {
            connections[direction.getId()] = PIPE;
        }
        for (Direction connection : this.connections) {
            connections[connection.getId()] = BLOCK;
        }
        return connections;
    }

    @Override
    public void removeConnection(World world, BlockPos pos, Direction direction) {
        // Remove if it exists
        for (int i = 0; i < connections.size(); i++) {
            if (connections.get(i) == direction) {
                connections.remove(i);
                return;
            }
        }
    }

    @Override
    public void addConnection(World world, BlockPos pos, Direction direction) {
        // Refuse if it already exists
        for (Direction connection : connections) {
            if (connection == direction) {
                return;
            }
        }
        // Otherwise try to connect
        if (canConnect(world, pos, direction)) {
            connections.add(direction);
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        tag.putByte("connections", NbtHelper.encodeDirections(connections));
        tag.putLong("eu", eu);
        return tag;
    }

    @Override
    public void fromTag(CompoundTag tag) {
        connections = new ArrayList<>(Arrays.asList(NbtHelper.decodeDirections(tag.getByte("connections"))));
        eu = tag.getLong("eu");
    }

    private boolean canConnect(World world, BlockPos pos, Direction direction) {
        SearchOption option = SearchOptions.inDirection(direction);
        EnergyInsertable insertable = EnergyAttributes.INSERTABLE.getFirstOrNull(world, pos.offset(direction), option);
        EnergyExtractable extractable = EnergyAttributes.EXTRACTABLE.getFirstOrNull(world, pos.offset(direction), option);
        CableTier tier = ((ElectricityNetwork) network).tier;
        return insertable != null && insertable.canInsert(tier) || extractable != null && extractable.canExtract(tier);
    }

    // Used in the Waila plugin
    public long getEu() {
        return eu;
    }

    public long getMaxEu() {
        return getTier().getMaxInsert();
    }

    public CableTier getTier() {
        return ((ElectricityNetwork) network).tier;
    }
}
