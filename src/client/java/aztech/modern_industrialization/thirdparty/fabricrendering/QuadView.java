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
package aztech.modern_industrialization.thirdparty.fabricrendering;
/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * Interface for reading quad data encoded by {@link MeshBuilder}. Enables models to do analysis, re-texturing or
 * translation without knowing the renderer's vertex formats and without retaining redundant information.
 *
 * <p>
 * Only the renderer should implement or extend this interface.
 */
public interface QuadView {
    /** Count of integers in a conventional (un-modded) block or item vertex. */
    int VANILLA_VERTEX_STRIDE = DefaultVertexFormat.BLOCK.getVertexSize() / 4;

    /** Count of integers in a conventional (un-modded) block or item quad. */
    int VANILLA_QUAD_STRIDE = VANILLA_VERTEX_STRIDE * 4;

    /**
     * Retrieve geometric position, x coordinate.
     */
    float x(int vertexIndex);

    /**
     * Retrieve geometric position, y coordinate.
     */
    float y(int vertexIndex);

    /**
     * Retrieve geometric position, z coordinate.
     */
    float z(int vertexIndex);

    /**
     * Convenience: access x, y, z by index 0-2.
     */
    float posByIndex(int vertexIndex, int coordinateIndex);

    /**
     * Pass a non-null target to avoid allocation - will be returned with values. Otherwise returns a new instance.
     */
    Vector3f copyPos(int vertexIndex, @Nullable Vector3f target);

    /**
     * Retrieve vertex color.
     */
    int color(int vertexIndex);

    /**
     * Retrieve horizontal texture coordinates.
     */
    float u(int vertexIndex);

    /**
     * Retrieve vertical texture coordinates.
     */
    float v(int vertexIndex);

    /**
     * Pass a non-null target to avoid allocation - will be returned with values. Otherwise returns a new instance.
     */
    default Vector2f copyUv(int vertexIndex, @Nullable Vector2f target) {
        if (target == null) {
            target = new Vector2f();
        }

        target.set(u(vertexIndex), v(vertexIndex));
        return target;
    }

    /**
     * Minimum block brightness. Zero if not set.
     */
    int lightmap(int vertexIndex);

    /**
     * If false, no vertex normal was provided. Lighting should use face normal in that case.
     */
    boolean hasNormal(int vertexIndex);

    /**
     * Will return {@link Float#NaN} if normal not present.
     */
    float normalX(int vertexIndex);

    /**
     * Will return {@link Float#NaN} if normal not present.
     */
    float normalY(int vertexIndex);

    /**
     * Will return {@link Float#NaN} if normal not present.
     */
    float normalZ(int vertexIndex);

    /**
     * Pass a non-null target to avoid allocation - will be returned with values. Otherwise returns a new instance.
     * Returns null if normal not present.
     */
    @Nullable
    Vector3f copyNormal(int vertexIndex, @Nullable Vector3f target);

    /**
     * If non-null, quad should not be rendered in-world if the opposite face of a neighbor block occludes it.
     *
     * @see MutableQuadView#cullFace(Direction)
     */
    @Nullable
    Direction cullFace();

    /**
     * Equivalent to {@link BakedQuad#getDirection()}. This is the face used for vanilla lighting calculations and will
     * be the block face to which the quad is most closely aligned. Always the same as cull face for quads that are on a
     * block face, but never null.
     */
    @NotNull
    Direction lightFace();

    /**
     * See {@link MutableQuadView#nominalFace(Direction)}.
     */
    Direction nominalFace();

    /**
     * Normal of the quad as implied by geometry. Will be invalid if quad vertices are not co-planar. Typically computed
     * lazily on demand and not encoded.
     *
     * <p>
     * Not typically needed by models. Exposed to enable standard lighting utility functions for use by renderers.
     */
    Vector3f faceNormal();

    /**
     * Retrieves the quad color index serialized with the quad.
     */
    int colorIndex();

    /**
     * Retrieves the integer tag encoded with this quad via {@link MutableQuadView#tag(int)}. Will return zero if no tag
     * was set. For use by models.
     */
    int tag();

    /**
     * Extracts all quad properties except material to the given {@link MutableQuadView} instance. Must be used before
     * calling {link QuadEmitter#emit()} on the target instance. Meant for re-texturing, analysis and static
     * transformation use cases.
     */
    void copyTo(MutableQuadView target);

    /**
     * Reads baked vertex data and outputs standard {@link BakedQuad#getVertices() baked quad vertex data} in the given
     * array and location.
     *
     * @param target      Target array for the baked quad data.
     *
     * @param targetIndex Starting position in target array - array must have at least 28 elements available at this
     *                    index.
     */
    void toVanilla(int[] target, int targetIndex);

    /**
     * Generates a new BakedQuad instance with texture coordinates and colors from the given sprite.
     *
     * @param sprite {@link MutableQuadView} does not serialize sprites so the sprite must be provided by the caller.
     *
     * @return A new baked quad instance with the closest-available appearance supported by vanilla features. Will
     *         retain emissive light maps, for example, but the standard Minecraft renderer will not use them.
     */
    default BakedQuad toBakedQuad(TextureAtlasSprite sprite) {
        int[] vertexData = new int[VANILLA_QUAD_STRIDE];
        toVanilla(vertexData, 0);
        // TODO material inspection: set shade as !disableDiffuse
        // TODO material inspection: set color index to -1 if the material disables it
        return new BakedQuad(vertexData, colorIndex(), lightFace(), sprite, true);
    }
}
