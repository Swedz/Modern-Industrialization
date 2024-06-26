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
package aztech.modern_industrialization.blocks.storage.tank;

import aztech.modern_industrialization.MIComponents;
import aztech.modern_industrialization.blocks.storage.AbstractStorageBlockItem;
import aztech.modern_industrialization.blocks.storage.ResourceStorage;
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.fluid.FluidVariant;
import aztech.modern_industrialization.util.FluidHelper;
import java.util.List;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public class TankItem extends AbstractStorageBlockItem<FluidVariant> {

    public TankItem(TankBlock block, Properties settings) {
        super(block, settings);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {

        if (this.behaviour.isCreative()) {
            tooltip.add(FluidHelper.getFluidName(getResource(stack), true));
        } else {
            long capacity = behaviour.getCapacityForResource(getResource(stack));

            if (isEmpty(stack)) {
                if (!isUnlocked(stack)) {
                    tooltip.addAll(FluidHelper.getTooltipForFluidStorage(getResource(stack), 0, capacity));
                } else {
                    tooltip.addAll(FluidHelper.getTooltipForFluidStorage(FluidVariant.blank(), 0, capacity));
                }
            } else {
                tooltip.addAll(FluidHelper.getTooltipForFluidStorage(getResource(stack), getAmount(stack), capacity));
            }
        }

        super.appendHoverText(stack, context, tooltip, flag);
    }

    @Override
    public DataComponentType<ResourceStorage<FluidVariant>> getComponentType() {
        return MIComponents.FLUID_STORAGE.get();
    }

    @Override
    public ResourceStorage<FluidVariant> getDefaultComponent() {
        return ResourceStorage.FLUID_EMPTY;
    }
}
