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

public class GroundPlaneBuilder {

    public static Model buildGround(ModelBuilder modelBuilder, float width, float height) {
        modelBuilder.begin();

        // Main grass plane
        Material grassMaterial = new Material(ColorAttribute.createDiffuse(new Color(0.3f, 0.6f, 0.2f, 1f)));
        MeshPartBuilder grassBuilder = modelBuilder.part("grass", GL20.GL_TRIANGLES,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal,
            grassMaterial);

        float halfWidth = width / 2;
        float halfHeight = height / 2;

        // Create quad for ground
        Vector3 corner00 = new Vector3(-halfWidth, 0, -halfHeight);
        Vector3 corner10 = new Vector3(halfWidth, 0, -halfHeight);
        Vector3 corner11 = new Vector3(halfWidth, 0, halfHeight);
        Vector3 corner01 = new Vector3(-halfWidth, 0, halfHeight);

        grassBuilder.rect(corner00, corner10, corner11, corner01, new Vector3(0, 1, 0));

        // Add some visual variety with paths
        Material pathMaterial = new Material(ColorAttribute.createDiffuse(new Color(0.5f, 0.45f, 0.35f, 1f)));
        MeshPartBuilder pathBuilder = modelBuilder.part("paths", GL20.GL_TRIANGLES,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal,
            pathMaterial);

        // Horizontal path
        float pathWidth = 3f;
        float pathY = 0.01f; // Slightly above grass
        pathBuilder.rect(
            new Vector3(-halfWidth, pathY, -pathWidth / 2),
            new Vector3(halfWidth, pathY, -pathWidth / 2),
            new Vector3(halfWidth, pathY, pathWidth / 2),
            new Vector3(-halfWidth, pathY, pathWidth / 2),
            new Vector3(0, 1, 0)
        );

        // Vertical path
        pathBuilder.rect(
            new Vector3(-pathWidth / 2, pathY, -halfHeight),
            new Vector3(pathWidth / 2, pathY, -halfHeight),
            new Vector3(pathWidth / 2, pathY, halfHeight),
            new Vector3(-pathWidth / 2, pathY, halfHeight),
            new Vector3(0, 1, 0)
        );

        return modelBuilder.end();
    }

    public static Model buildGridOverlay(ModelBuilder modelBuilder, float width, float height, float gridSize) {
        modelBuilder.begin();

        Material gridMaterial = new Material(ColorAttribute.createDiffuse(new Color(0.25f, 0.5f, 0.15f, 0.5f)));
        MeshPartBuilder gridBuilder = modelBuilder.part("grid", GL20.GL_LINES,
            VertexAttributes.Usage.Position,
            gridMaterial);

        float halfWidth = width / 2;
        float halfHeight = height / 2;
        float y = 0.02f;

        // Vertical lines
        for (float x = -halfWidth; x <= halfWidth; x += gridSize) {
            gridBuilder.line(new Vector3(x, y, -halfHeight), new Vector3(x, y, halfHeight));
        }

        // Horizontal lines
        for (float z = -halfHeight; z <= halfHeight; z += gridSize) {
            gridBuilder.line(new Vector3(-halfWidth, y, z), new Vector3(halfWidth, y, z));
        }

        return modelBuilder.end();
    }
}
