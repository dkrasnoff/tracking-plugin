package ru.krasnov.jetbrains.plugin;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BuildTrackerFunctionalTest {

    @TempDir
    File testProjectDir;
    private File settingsFile;
    private File buildFile;

    @BeforeEach
    public void setup() {
        settingsFile = new File(testProjectDir, "settings.gradle");
        buildFile = new File(testProjectDir, "build.gradle");
    }

    /**
     * Test checks that our plugin does not break the build
     */
    @Test
    public void testHelloWorldTask() throws IOException {
        writeFile(settingsFile,
                "pluginManagement {\n" +
                        "    repositories {\n" +
                        "        mavenLocal()\n" +
                        "    }\n" +
                        "}\n" +
                        "rootProject.name = 'hello-world'");
        String buildFileContent = "" +
                "plugins {\n" +
                "    id(\"ru.krasnov.jetbrains.tracking-plugin\") version \"1.0.0\"\n" +
                "}\n" +
                "task helloWorld {" +
                "    doLast {" +
                "        println 'Hello world!'" +
                "    }" +
                "}";
        writeFile(buildFile, buildFileContent);

        BuildResult result = GradleRunner.create()
                .withProjectDir(testProjectDir)
                .withArguments("helloWorld")
                .build();

        assertTrue(result.getOutput().contains("Hello world!"));
        assertEquals(SUCCESS, result.task(":helloWorld").getOutcome());
    }

    private void writeFile(File destination, String content) throws IOException {
        BufferedWriter output = null;
        try {
            output = new BufferedWriter(new FileWriter(destination));
            output.write(content);
        } finally {
            if (output != null) {
                output.close();
            }
        }
    }
}
