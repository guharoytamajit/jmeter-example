package com;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.config.gui.ArgumentsPanel;
import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.control.gui.LoopControlPanel;
import org.apache.jmeter.control.gui.TestPlanGui;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.protocol.http.control.gui.HttpTestSampleGui;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.threads.gui.ThreadGroupGui;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;

public class Main2 {
public static void main(String[] args) throws FileNotFoundException, IOException {

	
	 String JMETER_HOME="C:/tamajit/programs/apache-jmeter-4.0";
	//Set jmeter home for the jmeter utils to load
    File jmeterHome = new File(JMETER_HOME);
    String slash = System.getProperty("file.separator");

    if (jmeterHome.exists()) {
        File jmeterProperties = new File(jmeterHome.getPath() + slash + "bin" + slash + "jmeter.properties");
        if (jmeterProperties.exists()) {
            //JMeter Engine
            StandardJMeterEngine jmeter = new StandardJMeterEngine();

            //JMeter initialization (properties, log levels, locale, etc)
            JMeterUtils.setJMeterHome(jmeterHome.getPath());
            JMeterUtils.loadJMeterProperties(jmeterProperties.getPath());
            JMeterUtils.initLogging();// you can comment this line out to see extra log messages of i.e. DEBUG level
            JMeterUtils.initLocale();

            // JMeter Test Plan, basically JOrphan HashTree
            HashTree testPlanTree = new HashTree();

            // First HTTP Sampler - open uttesh.com
            HTTPSamplerProxy examplecomSampler = new HTTPSamplerProxy();
            examplecomSampler.setDomain("localhost");
            examplecomSampler.setPort(8080);
            examplecomSampler.setPath("/");
            examplecomSampler.setMethod("GET");
            examplecomSampler.setName("Open uttesh.com");
            examplecomSampler.setProperty(TestElement.TEST_CLASS, HTTPSamplerProxy.class.getName());
            examplecomSampler.setProperty(TestElement.GUI_CLASS, HttpTestSampleGui.class.getName());


            // Loop Controller
            LoopController loopController = new LoopController();
            loopController.setLoops(1);
            loopController.setFirst(true);
            loopController.setProperty(TestElement.TEST_CLASS, LoopController.class.getName());
            loopController.setProperty(TestElement.GUI_CLASS, LoopControlPanel.class.getName());
            loopController.initialize();

            // Thread Group
            org.apache.jmeter.threads.ThreadGroup threadGroup = new org.apache.jmeter.threads.ThreadGroup();
            threadGroup.setName("Sample Thread Group");
            threadGroup.setNumThreads(10);
            threadGroup.setRampUp(1);
            threadGroup.setSamplerController(loopController);
            threadGroup.setProperty(TestElement.TEST_CLASS, ThreadGroup.class.getName());
            threadGroup.setProperty(TestElement.GUI_CLASS, ThreadGroupGui.class.getName());

            // Test Plan
            TestPlan testPlan = new TestPlan("Create JMeter Script From Java Code");
            
            testPlan.setProperty(TestElement.TEST_CLASS, TestPlan.class.getName());
            testPlan.setProperty(TestElement.GUI_CLASS, TestPlanGui.class.getName());
            testPlan.setUserDefinedVariables((Arguments) new ArgumentsPanel().createTestElement());

            // Construct Test Plan from previously initialized elements
            testPlanTree.add(testPlan);
            HashTree threadGroupHashTree = testPlanTree.add(testPlan, threadGroup);
            threadGroupHashTree.add(examplecomSampler);

            // save generated test plan to JMeter's .jmx file format
            SaveService.saveTree(testPlanTree, new FileOutputStream("report/jmeter_api_sample.jmx"));

            //add Summarizer output to get test progress in stdout like:
            // summary =      2 in   1.3s =    1.5/s Avg:   631 Min:   290 Max:   973 Err:     0 (0.00%)
            Summariser summer = null;
            String summariserName = JMeterUtils.getPropDefault("summariser.name", "summary");
            if (summariserName.length() > 0) {
                summer = new Summariser(summariserName);
            }

//            SaveService.saveSampleResult(testPlanTree, new FileOutputStream("report/jmeter_api_sample.jmx"));

            // Store execution results into a .jtl file, we can save file as csv also
            String reportFile = "report/report.jtl";
            String csvFile = "report/report.csv";
            ResultCollector logger = new ResultCollector(summer);
            logger.setFilename(reportFile);
            
            ResultCollector csvlogger = new ResultCollector(summer);
            csvlogger.setFilename(csvFile);
         
            
            testPlanTree.add(testPlanTree.getArray()[0], logger);
            testPlanTree.add(testPlanTree.getArray()[0], csvlogger);
            
            
            
            // Run Test Plan
            jmeter.configure(testPlanTree);
            jmeter.run();
            
            try {
//                Process process = Runtime.getRuntime().exec(command);
//            	 ProcessBuilder pb = new ProcessBuilder("export MY_ENV_VAR=1");
            	
//            	System.setProperty("JMETER_HOME", jmeterHome.getPath());
                Process process = Runtime.getRuntime().exec(jmeterHome.getPath() + slash + "bin" + slash +"jmeter.bat -g report/report.jtl -o /html");
             
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
             
                reader.close();
             
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Test completed. See " + jmeterHome + slash + "report.jtl file for results");
            System.out.println("JMeter .jmx script is available at " + jmeterHome + slash + "jmeter_api_sample.jmx");
            System.exit(0);

        }
    }

    System.err.println("jmeterHome property is not set or pointing to incorrect location");
    System.exit(1);
}
}
