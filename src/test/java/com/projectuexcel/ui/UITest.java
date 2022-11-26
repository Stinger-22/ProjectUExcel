package com.projectuexcel.ui;

import javafx.scene.input.KeyCode;
import org.junit.Before;
import org.junit.Test;
import org.testfx.api.FxAssert;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;
import org.testfx.matcher.control.TextInputControlMatchers;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

public class UITest extends ApplicationTest {

    @Before
    public void before() throws Exception {
        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(ApplicationWindow.class);
    }

    @Test
    public void appearanceTest() {
        FxAssert.verifyThat("#choosePlan", LabeledMatchers.hasText("Choose plan"));
        FxAssert.verifyThat("#SendOrigin", LabeledMatchers.hasText("Send origin file"));
        FxAssert.verifyThat("#SendPartAll", LabeledMatchers.hasText("Send part to all"));
        FxAssert.verifyThat("#SendPartOne", LabeledMatchers.hasText("Send part to one"));
        FxAssert.verifyThat("#ExportAll", LabeledMatchers.hasText("Export all"));
        FxAssert.verifyThat("#ExportOne", LabeledMatchers.hasText("Export one"));
        FxAssert.verifyThat("#selectFirstSemester", LabeledMatchers.hasText("1st semester"));
        FxAssert.verifyThat("#selectSecondSemester", LabeledMatchers.hasText("2nd semester"));
        FxAssert.verifyThat("#selectYear", LabeledMatchers.hasText("year"));
    }

    @Test
    public void choosePlanTest() {
        clickOn("#choosePlan");
        String pathPlan = System.getProperty("user.dir") + "\\UITest.xlsx";
        copyText(pathPlan);
        FxAssert.verifyThat("#pathPlan", TextInputControlMatchers.hasText(pathPlan));
    }

    @Test
    public void setupSubjectTest() {
        clickOn("#subject");
        String subject = "SubjectTest";
        copyText(subject);
        FxAssert.verifyThat("#subject", TextInputControlMatchers.hasText(subject));
    }

    @Test
    public void setupTextTest() {
        clickOn("#text");
        String text = "TextTest";
        copyText(text);
        // Can't verify. TestFX can't retrieve text from HTMLEditor
    }

    private void copyText(String toCopy) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection stringSelection = new StringSelection(toCopy);
        clipboard.setContents(stringSelection, stringSelection);

        press(KeyCode.CONTROL).press(KeyCode.V).release(KeyCode.V).release(KeyCode.CONTROL);
        push(KeyCode.ENTER);
    }
}
