package com.projectuexcel.ui;

import javafx.scene.Node;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.web.HTMLEditor;
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
import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.assertEquals;

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
        HTMLEditor editor = lookup("#text").query();
        assertEquals("<html dir=\"ltr\"><head></head><body contenteditable=\"true\"><p><span style=\"font-family: &quot;&quot;;\">TextTest</span></p><p><span style=\"font-family: &quot;&quot;;\"><br></span></p></body></html>", editor.getHtmlText());
    }

    private void copyText(String toCopy) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection stringSelection = new StringSelection(toCopy);
        clipboard.setContents(stringSelection, stringSelection);

        press(KeyCode.CONTROL).press(KeyCode.V).release(KeyCode.V).release(KeyCode.CONTROL);
        push(KeyCode.ENTER);
    }

    @Test
    public void historyOpenTest() throws IOException {
        clickOn("#HistoryTab");
        TableView<Map> table = lookup("#planHistory").query();
        Node node = lookup("#fileNameColumn").nth(1).query();
        doubleClickOn(node);
        Runtime rt = Runtime.getRuntime();
        rt.exec("taskkill /F /IM excel.exe");
    }

    @Test
    public void emailTest() {
        clickOn("#EmailsTab");
        clickOn("#changeEmails");

    }
}
