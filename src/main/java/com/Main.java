package com;

import java.io.File;
import java.io.IOException;

import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.protocol.http.sampler.HTTPSampler;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;
import org.apache.jmeter.threads.ThreadGroup;

public class Main {
public static void main(String[] args) throws IOException {
    StandardJMeterEngine jmeter = new StandardJMeterEngine();
String JMETER_HOME="C:/tamajit/programs/apache-jmeter-4.0";

    // Initialize Properties, logging, locale, etc.
    JMeterUtils.loadJMeterProperties(JMETER_HOME+"/bin/jmeter.properties");
    JMeterUtils.setJMeterHome(JMETER_HOME);
    JMeterUtils.initLogging();// you can comment this line out to see extra log messages of i.e. DEBUG level
    JMeterUtils.initLocale();

    // Initialize JMeter SaveService
    SaveService.loadProperties();

    // Load existing .jmx Test Plan
   // FileInputStream in = new FileInputStream(JMETER_HOME+"/"+tamajit.jmx);
    HashTree testPlanTree = SaveService.loadTree(new File(JMETER_HOME+"/"+"tamajit.jmx"));
//    in.close();

    
    
    Summariser summer = null;
    String summariserName = JMeterUtils.getPropDefault("summariser.name", "summary");
    if (summariserName.length() > 0) {
        summer = new Summariser(summariserName);
    }
    String reportFile = "report/report.jtl";
    String csvFile = "report/report.csv";
    ResultCollector logger = new ResultCollector(summer);
    logger.setFilename(reportFile);
    ResultCollector csvlogger = new ResultCollector(summer);
    csvlogger.setFilename(csvFile);
    testPlanTree.add(testPlanTree.getArray()[0], logger);
    testPlanTree.add(testPlanTree.getArray()[0], csvlogger);
    
    
    
    // Run JMeter Test
    jmeter.configure(testPlanTree);
    jmeter.run();
}
}
