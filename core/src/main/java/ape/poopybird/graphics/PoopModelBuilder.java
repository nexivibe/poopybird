package ape.poopybird.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;

public class PoopModelBuilder {

    private static final Color POOP_COLOR = new Color(0.45f, 0.3f, 0.15f, 1f);
    private static final Color POOP_HIGHLIGHT = new Color(0.5f, 0.35f, 0.2f, 1f);

    public static Model buildPoop(ModelBuilder modelBuilder) {
        modelBuilder.begin();

        Material poopMaterial = new Material(ColorAttribute.createDiffuse(POOP_COLOR));
        MeshPartBuilder builder = modelBuilder.part("poop", GL20.GL_TRIANGLES,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal,
            poopMaterial);

        // Classic poop swirl shape - stack of decreasing spheroid layers
        float baseRadius = 0.15f;
        float y = 0;

        // Bottom blob
        builder.box(0, y + 0.05f, 0, baseRadius * 2, 0.1f, baseRadius * 2);

        // Middle blob
        builder.box(0, y + 0.12f, 0, baseRadius * 1.6f, 0.08f, baseRadius * 1.6f);

        // Top blob
        builder.box(0, y + 0.18f, 0, baseRadius * 1.2f, 0.06f, baseRadius * 1.2f);

        // Tip
        Material highlightMaterial = new Material(ColorAttribute.createDiffuse(POOP_HIGHLIGHT));
        MeshPartBuilder tipBuilder = modelBuilder.part("tip", GL20.GL_TRIANGLES,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal,
            highlightMaterial);

        // Pointed tip
        Vector3 tipBase1 = new Vector3(-baseRadius * 0.4f, y + 0.21f, -baseRadius * 0.4f);
        Vector3 tipBase2 = new Vector3(baseRadius * 0.4f, y + 0.21f, -baseRadius * 0.4f);
        Vector3 tipBase3 = new Vector3(baseRadius * 0.4f, y + 0.21f, baseRadius * 0.4f);
        Vector3 tipBase4 = new Vector3(-baseRadius * 0.4f, y + 0.21f, baseRadius * 0.4f);
        Vector3 tipTop = new Vector3(0, y + 0.3f, 0);

        tipBuilder.triangle(tipBase1, tipBase2, tipTop);
        tipBuilder.triangle(tipBase2, tipBase3, tipTop);
        tipBuilder.triangle(tipBase3, tipBase4, tipTop);
        tipBuilder.triangle(tipBase4, tipBase1, tipTop);

        return modelBuilder.end();
    }

    public static Model buildSplat(ModelBuilder modelBuilder) {
        modelBuilder.begin();

        Material splatMaterial = new Material(ColorAttribute.createDiffuse(POOP_COLOR));
        MeshPartBuilder builder = modelBuilder.part("splat", GL20.GL_TRIANGLES,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal,
            splatMaterial);

        // Flat splat shape
        builder.box(0, 0.01f, 0, 0.4f, 0.02f, 0.4f);

        // Splatter spots
        builder.box(0.2f, 0.01f, 0.15f, 0.1f, 0.015f, 0.08f);
        builder.box(-0.15f, 0.01f, 0.2f, 0.08f, 0.015f, 0.1f);
        builder.box(-0.18f, 0.01f, -0.12f, 0.12f, 0.015f, 0.06f);
        builder.box(0.1f, 0.01f, -0.2f, 0.06f, 0.015f, 0.08f);

        return modelBuilder.end();
    }

    public static Model buildPowerUp(ModelBuilder modelBuilder) {
        modelBuilder.begin();

        // Glowing orb
        Material orbMaterial = new Material(ColorAttribute.createDiffuse(new Color(1f, 0.9f, 0.3f, 1f)));
        MeshPartBuilder orbBuilder = modelBuilder.part("orb", GL20.GL_TRIANGLES,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal,
            orbMaterial);

        // Simple octahedron shape for power-up
        float size = 0.5f;
        Vector3 top = new Vector3(0, size, 0);
        Vector3 bottom = new Vector3(0, -size, 0);
        Vector3 front = new Vector3(0, 0, size);
        Vector3 back = new Vector3(0, 0, -size);
        Vector3 left = new Vector3(-size, 0, 0);
        Vector3 right = new Vector3(size, 0, 0);

        // Top pyramid
        orbBuilder.triangle(top, front, right);
        orbBuilder.triangle(top, right, back);
        orbBuilder.triangle(top, back, left);
        orbBuilder.triangle(top, left, front);

        // Bottom pyramid
        orbBuilder.triangle(bottom, right, front);
        orbBuilder.triangle(bottom, back, right);
        orbBuilder.triangle(bottom, left, back);
        orbBuilder.triangle(bottom, front, left);

        return modelBuilder.end();
    }
}
