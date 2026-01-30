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
import ape.poopybird.entities.BirdType;

public class OrigamiBirdBuilder {

    public static Model buildBird(ModelBuilder modelBuilder, BirdType birdType) {
        float scale = birdType.getScale();
        float wingSize = birdType.getWingSize();
        Color bodyColor = getBodyColor(birdType);
        Color wingColor = getWingColor(birdType);

        modelBuilder.begin();

        // Body - triangular prism shape
        Material bodyMaterial = new Material(ColorAttribute.createDiffuse(bodyColor));
        MeshPartBuilder bodyBuilder = modelBuilder.part("body", GL20.GL_TRIANGLES,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal,
            bodyMaterial);

        // Body vertices (pointed beak forward along +Z)
        float bodyLength = 1.5f * scale;
        float bodyWidth = 0.5f * scale;
        float bodyHeight = 0.4f * scale;

        // Front point (beak)
        Vector3 beak = new Vector3(0, 0, bodyLength / 2);
        // Back top
        Vector3 backTop = new Vector3(0, bodyHeight, -bodyLength / 2);
        // Back bottom left
        Vector3 backBottomLeft = new Vector3(-bodyWidth / 2, -bodyHeight / 2, -bodyLength / 2);
        // Back bottom right
        Vector3 backBottomRight = new Vector3(bodyWidth / 2, -bodyHeight / 2, -bodyLength / 2);

        // Top face
        bodyBuilder.triangle(beak, backTop, backBottomLeft);
        bodyBuilder.triangle(beak, backBottomRight, backTop);
        // Bottom face
        bodyBuilder.triangle(beak, backBottomLeft, backBottomRight);
        // Back face
        bodyBuilder.triangle(backTop, backBottomRight, backBottomLeft);

        // Wings - flat triangles
        Material wingMaterial = new Material(ColorAttribute.createDiffuse(wingColor));
        MeshPartBuilder wingBuilder = modelBuilder.part("wings", GL20.GL_TRIANGLES,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal,
            wingMaterial);

        float wingLength = wingSize * scale;
        float wingDepth = 0.8f * scale;

        // Left wing
        Vector3 lwBase1 = new Vector3(-bodyWidth / 2, 0, 0);
        Vector3 lwBase2 = new Vector3(-bodyWidth / 2, 0, -wingDepth);
        Vector3 lwTip = new Vector3(-wingLength, 0, -wingDepth / 2);
        wingBuilder.triangle(lwBase1, lwTip, lwBase2);
        wingBuilder.triangle(lwBase2, lwTip, lwBase1); // Double-sided

        // Right wing
        Vector3 rwBase1 = new Vector3(bodyWidth / 2, 0, 0);
        Vector3 rwBase2 = new Vector3(bodyWidth / 2, 0, -wingDepth);
        Vector3 rwTip = new Vector3(wingLength, 0, -wingDepth / 2);
        wingBuilder.triangle(rwBase1, rwBase2, rwTip);
        wingBuilder.triangle(rwTip, rwBase2, rwBase1); // Double-sided

        // Tail - small triangle at back
        Material tailMaterial = new Material(ColorAttribute.createDiffuse(wingColor.cpy().mul(0.8f)));
        MeshPartBuilder tailBuilder = modelBuilder.part("tail", GL20.GL_TRIANGLES,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal,
            tailMaterial);

        float tailLength = 0.4f * scale;
        float tailWidth = 0.3f * scale;
        Vector3 tailBase = new Vector3(0, bodyHeight * 0.5f, -bodyLength / 2);
        Vector3 tailLeft = new Vector3(-tailWidth, bodyHeight * 0.3f, -bodyLength / 2 - tailLength);
        Vector3 tailRight = new Vector3(tailWidth, bodyHeight * 0.3f, -bodyLength / 2 - tailLength);

        tailBuilder.triangle(tailBase, tailLeft, tailRight);
        tailBuilder.triangle(tailBase, tailRight, tailLeft); // Double-sided

        return modelBuilder.end();
    }

    private static Color getBodyColor(BirdType type) {
        switch (type) {
            case SPARROW: return new Color(0.6f, 0.45f, 0.3f, 1f);  // Brown
            case PIGEON: return new Color(0.5f, 0.5f, 0.55f, 1f);   // Gray
            case CROW: return new Color(0.15f, 0.15f, 0.2f, 1f);    // Black
            case SEAGULL: return new Color(0.95f, 0.95f, 0.95f, 1f); // White
            case HAWK: return new Color(0.55f, 0.35f, 0.2f, 1f);    // Brown-red
            case EAGLE: return new Color(0.25f, 0.2f, 0.15f, 1f);   // Dark brown
            default: return Color.GRAY;
        }
    }

    private static Color getWingColor(BirdType type) {
        switch (type) {
            case SPARROW: return new Color(0.5f, 0.35f, 0.2f, 1f);
            case PIGEON: return new Color(0.4f, 0.4f, 0.5f, 1f);
            case CROW: return new Color(0.1f, 0.1f, 0.15f, 1f);
            case SEAGULL: return new Color(0.3f, 0.3f, 0.35f, 1f);  // Gray wing tips
            case HAWK: return new Color(0.45f, 0.25f, 0.15f, 1f);
            case EAGLE: return new Color(0.2f, 0.15f, 0.1f, 1f);
            default: return Color.DARK_GRAY;
        }
    }
}
