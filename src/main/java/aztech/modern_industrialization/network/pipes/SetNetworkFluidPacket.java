/*
 * MIT License
 *
 * Copyright (c) 2020 Azercoco & Technici4n
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package aztech.modern_industrialization.network.pipes;

import aztech.modern_industrialization.network.BasePacket;
import aztech.modern_industrialization.pipes.fluid.FluidPipeScreenHandler;
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.fluid.FluidVariant;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.inventory.AbstractContainerMenu;

public record SetNetworkFluidPacket(int syncId, FluidVariant fluid) implements BasePacket {
    public static final StreamCodec<RegistryFriendlyByteBuf, SetNetworkFluidPacket> STREAM_CODEC = StreamCodec.ofMember(
            SetNetworkFluidPacket::write, SetNetworkFluidPacket::new);

    public SetNetworkFluidPacket(RegistryFriendlyByteBuf buf) {
        this(buf.readUnsignedByte(), FluidVariant.fromPacket(buf));
    }

    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeByte(syncId);
        fluid.toPacket(buf);
    }

    @Override
    public void handle(Context ctx) {
        AbstractContainerMenu handler = ctx.getPlayer().containerMenu;
        if (handler.containerId == syncId) {
            ((FluidPipeScreenHandler) handler).iface.setNetworkFluid(fluid);
        }
    }
}
