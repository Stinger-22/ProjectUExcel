package com.projectuexcel.ui;

import com.projectuexcel.ui.table.CodeMail;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.web.HTMLEditor;
import org.junit.Before;
import org.junit.Test;
import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;
import org.testfx.matcher.control.TextInputControlMatchers;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

        Process process = Runtime.getRuntime().exec(System.getenv("windir") +"\\system32\\"+"tasklist.exe /nh");
        String line;

        boolean excelExecuted = false;
        BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
        while ((line = input.readLine()) != null) {
            if (line.contains("EXCEL.EXE")) {
                excelExecuted = true;
                break;
            }
        }
        input.close();
        assertTrue(excelExecuted);
        Runtime.getRuntime().exec("taskkill /f /IM excel.exe");

    }

    @Test
    public void emailTest() {
        clickOn("#EmailsTab");
        clickOn("#changeEmails");
        String pathEmails = System.getProperty("user.dir") + "\\UITest.txt";
        copyText(pathEmails);

        TableView<Map> table = lookup("#codeMailTableView").query();
        Node node;

        ObservableList<Map> codeMailObservableList = table.getItems();

        node = lookup("#codeColumn").nth(0).query();
        clickOn(node);
        assertEquals(new CodeMail("ААА", "maksym.sobol.kn.2021@lpnu.ua"), codeMailObservableList.get(0));
        clickOn(node);
        assertEquals(new CodeMail("БББ", "maxym.sobol@gmail.com"), codeMailObservableList.get(0));

        clickOn("#addCode");
        copyText("ВВВ");
        clickOn("#addEmail");
        copyText("test@gmail.com");
        clickOn("#buttonAddCode");

        assertEquals(new CodeMail("ВВВ", "test@gmail.com"), codeMailObservableList.get(0));
        node = lookup("#codeColumn").nth(1).query();
        clickOn(node);
        push(KeyCode.DELETE);
        assertEquals(new CodeMail("БББ", "maxym.sobol@gmail.com"), codeMailObservableList.get(0));
    }

    @Test
    public void exportOneTest() {
        clickOn("#choosePlan");
        String pathPlan = System.getProperty("user.dir") + "\\UITest.xlsx";
        copyText(pathPlan);
        clickOn("#ExportOne");
        FxRobot robot = targetWindow("Export one");
        robot.clickOn("#pathFolder");
        copyText(System.getProperty("user.dir") + "\\");
        robot.clickOn("#code");
        copyText("ААА");
        robot.clickOn("#buttonOk");
        File folder = new File(System.getProperty("user.dir") + "\\");
        File[] files = folder.listFiles();

        boolean imported = false;
        for (File file : files) {
            if (file.isFile() && file.getName().equals("ААА.xlsx")) {
                imported = true;
                file.delete();
                break;
            }
        }
        assertTrue(imported);
    }

    @Test
    public void exportAllTest() {
        clickOn("#choosePlan");
        String pathPlan = System.getProperty("user.dir") + "\\UITest.xlsx";
        copyText(pathPlan);
        clickOn("#ExportAll");
        copyText(System.getProperty("user.dir"));
        push(KeyCode.ENTER);
        File folder = new File(System.getProperty("user.dir") + "\\");
        File[] files = folder.listFiles();

        int imported = 0;
        for (File file : files) {
            if (file.isFile()) {
                if (file.getName().equals("ААА.xlsx")) {
                    imported ^= 1;
                    file.delete();
                }
                else if (file.getName().equals("БББ.xlsx")) {
                    imported ^= 2;
                    file.delete();
                }
            }
            if (imported == 3) {
                break;
            }
        }
        assertEquals(3, imported);
    }
}
