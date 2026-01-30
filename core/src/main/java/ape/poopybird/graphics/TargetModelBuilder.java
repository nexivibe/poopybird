package ape.poopybird.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import ape.poopybird.entities.TargetType;

public class TargetModelBuilder {

    public static Model buildTarget(ModelBuilder modelBuilder, TargetType type) {
        switch (type) {
            case PERSON: return buildPerson(modelBuilder);
            case CAR: return buildCar(modelBuilder);
            case BENCH: return buildBench(modelBuilder);
            case STATUE: return buildStatue(modelBuilder);
            case UMBRELLA: return buildUmbrella(modelBuilder);
            case PICNIC: return buildPicnic(modelBuilder);
            default: return buildPerson(modelBuilder);
        }
    }

    private static Model buildPerson(ModelBuilder modelBuilder) {
        modelBuilder.begin();

        // Body (cylinder-ish using box for simplicity)
        Material shirtMaterial = new Material(ColorAttribute.createDiffuse(new Color(0.2f, 0.4f, 0.7f, 1f)));
        MeshPartBuilder bodyBuilder = modelBuilder.part("body", GL20.GL_TRIANGLES,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal,
            shirtMaterial);
        bodyBuilder.box(0, 0.6f, 0, 0.4f, 0.8f, 0.25f);

        // Head (smaller box)
        Material skinMaterial = new Material(ColorAttribute.createDiffuse(new Color(0.9f, 0.75f, 0.65f, 1f)));
        MeshPartBuilder headBuilder = modelBuilder.part("head", GL20.GL_TRIANGLES,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal,
            skinMaterial);
        headBuilder.box(0, 1.3f, 0, 0.3f, 0.35f, 0.3f);

        // Legs
        Material pantsMaterial = new Material(ColorAttribute.createDiffuse(new Color(0.25f, 0.25f, 0.3f, 1f)));
        MeshPartBuilder legsBuilder = modelBuilder.part("legs", GL20.GL_TRIANGLES,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal,
            pantsMaterial);
        legsBuilder.box(-0.1f, 0.1f, 0, 0.15f, 0.4f, 0.2f);
        legsBuilder.box(0.1f, 0.1f, 0, 0.15f, 0.4f, 0.2f);

        return modelBuilder.end();
    }

    private static Model buildCar(ModelBuilder modelBuilder) {
        modelBuilder.begin();

        // Car body
        Material bodyMaterial = new Material(ColorAttribute.createDiffuse(new Color(0.7f, 0.15f, 0.15f, 1f)));
        MeshPartBuilder bodyBuilder = modelBuilder.part("body", GL20.GL_TRIANGLES,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal,
            bodyMaterial);
        bodyBuilder.box(0, 0.4f, 0, 2.0f, 0.5f, 1.0f);

        // Car roof
        bodyBuilder.box(0, 0.8f, 0, 1.2f, 0.4f, 0.9f);

        // Wheels
        Material wheelMaterial = new Material(ColorAttribute.createDiffuse(Color.DARK_GRAY));
        MeshPartBuilder wheelBuilder = modelBuilder.part("wheels", GL20.GL_TRIANGLES,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal,
            wheelMaterial);
        wheelBuilder.box(-0.7f, 0.15f, 0.5f, 0.3f, 0.3f, 0.1f);
        wheelBuilder.box(0.7f, 0.15f, 0.5f, 0.3f, 0.3f, 0.1f);
        wheelBuilder.box(-0.7f, 0.15f, -0.5f, 0.3f, 0.3f, 0.1f);
        wheelBuilder.box(0.7f, 0.15f, -0.5f, 0.3f, 0.3f, 0.1f);

        // Windows
        Material windowMaterial = new Material(ColorAttribute.createDiffuse(new Color(0.6f, 0.8f, 0.9f, 1f)));
        MeshPartBuilder windowBuilder = modelBuilder.part("windows", GL20.GL_TRIANGLES,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal,
            windowMaterial);
        windowBuilder.box(0, 0.8f, 0.46f, 1.1f, 0.35f, 0.01f);
        windowBuilder.box(0, 0.8f, -0.46f, 1.1f, 0.35f, 0.01f);

        return modelBuilder.end();
    }

    private static Model buildBench(ModelBuilder modelBuilder) {
        modelBuilder.begin();

        Material woodMaterial = new Material(ColorAttribute.createDiffuse(new Color(0.5f, 0.35f, 0.2f, 1f)));
        MeshPartBuilder benchBuilder = modelBuilder.part("bench", GL20.GL_TRIANGLES,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal,
            woodMaterial);

        // Seat
        benchBuilder.box(0, 0.4f, 0, 1.5f, 0.1f, 0.4f);

        // Back
        benchBuilder.box(0, 0.7f, -0.15f, 1.5f, 0.5f, 0.08f);

        // Legs
        Material metalMaterial = new Material(ColorAttribute.createDiffuse(Color.DARK_GRAY));
        MeshPartBuilder legBuilder = modelBuilder.part("legs", GL20.GL_TRIANGLES,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal,
            metalMaterial);
        legBuilder.box(-0.6f, 0.2f, 0, 0.08f, 0.4f, 0.35f);
        legBuilder.box(0.6f, 0.2f, 0, 0.08f, 0.4f, 0.35f);

        return modelBuilder.end();
    }

    private static Model buildStatue(ModelBuilder modelBuilder) {
        modelBuilder.begin();

        // Pedestal
        Material stoneMaterial = new Material(ColorAttribute.createDiffuse(new Color(0.6f, 0.6f, 0.6f, 1f)));
        MeshPartBuilder baseBuilder = modelBuilder.part("base", GL20.GL_TRIANGLES,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal,
            stoneMaterial);
        baseBuilder.box(0, 0.3f, 0, 1.0f, 0.6f, 1.0f);

        // Statue figure
        Material bronzeMaterial = new Material(ColorAttribute.createDiffuse(new Color(0.5f, 0.4f, 0.25f, 1f)));
        MeshPartBuilder figureBuilder = modelBuilder.part("figure", GL20.GL_TRIANGLES,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal,
            bronzeMaterial);
        // Body
        figureBuilder.box(0, 1.2f, 0, 0.5f, 1.2f, 0.35f);
        // Head
        figureBuilder.box(0, 2.1f, 0, 0.35f, 0.4f, 0.35f);
        // Arms raised
        figureBuilder.box(-0.4f, 1.6f, 0, 0.3f, 0.15f, 0.15f);
        figureBuilder.box(0.4f, 1.6f, 0, 0.3f, 0.15f, 0.15f);

        return modelBuilder.end();
    }

    private static Model buildUmbrella(ModelBuilder modelBuilder) {
        modelBuilder.begin();

        // Pole
        Material poleMaterial = new Material(ColorAttribute.createDiffuse(new Color(0.4f, 0.3f, 0.2f, 1f)));
        MeshPartBuilder poleBuilder = modelBuilder.part("pole", GL20.GL_TRIANGLES,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal,
            poleMaterial);
        poleBuilder.box(0, 1.1f, 0, 0.08f, 2.2f, 0.08f);

        // Canopy (simplified as a flat disc-like shape)
        Material canopyMaterial = new Material(ColorAttribute.createDiffuse(new Color(0.9f, 0.3f, 0.3f, 1f)));
        MeshPartBuilder canopyBuilder = modelBuilder.part("canopy", GL20.GL_TRIANGLES,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal,
            canopyMaterial);

        // Octagonal canopy approximation
        float radius = 1.2f;
        float y = 2.0f;
        int segments = 8;
        for (int i = 0; i < segments; i++) {
            float angle1 = (float) (i * Math.PI * 2 / segments);
            float angle2 = (float) ((i + 1) * Math.PI * 2 / segments);
            float x1 = (float) Math.cos(angle1) * radius;
            float z1 = (float) Math.sin(angle1) * radius;
            float x2 = (float) Math.cos(angle2) * radius;
            float z2 = (float) Math.sin(angle2) * radius;

            canopyBuilder.triangle(
                new com.badlogic.gdx.math.Vector3(0, y + 0.2f, 0),
                new com.badlogic.gdx.math.Vector3(x1, y, z1),
                new com.badlogic.gdx.math.Vector3(x2, y, z2)
            );
        }

        return modelBuilder.end();
    }

    private static Model buildPicnic(ModelBuilder modelBuilder) {
        modelBuilder.begin();

        // Blanket
        Material blanketMaterial = new Material(ColorAttribute.createDiffuse(new Color(0.8f, 0.2f, 0.2f, 1f)));
        MeshPartBuilder blanketBuilder = modelBuilder.part("blanket", GL20.GL_TRIANGLES,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal,
            blanketMaterial);
        blanketBuilder.box(0, 0.02f, 0, 2.0f, 0.04f, 2.0f);

        // Basket
        Material basketMaterial = new Material(ColorAttribute.createDiffuse(new Color(0.6f, 0.45f, 0.25f, 1f)));
        MeshPartBuilder basketBuilder = modelBuilder.part("basket", GL20.GL_TRIANGLES,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal,
            basketMaterial);
        basketBuilder.box(0.5f, 0.15f, 0.3f, 0.4f, 0.25f, 0.3f);

        // Food items (simple boxes)
        Material foodMaterial = new Material(ColorAttribute.createDiffuse(new Color(0.9f, 0.8f, 0.3f, 1f)));
        MeshPartBuilder foodBuilder = modelBuilder.part("food", GL20.GL_TRIANGLES,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal,
            foodMaterial);
        foodBuilder.box(-0.3f, 0.08f, -0.2f, 0.2f, 0.12f, 0.2f);
        foodBuilder.box(0, 0.08f, 0.5f, 0.15f, 0.12f, 0.15f);

        return modelBuilder.end();
    }
}
