package com.github.elibracha;

import com.github.elibracha.model.ChangedOpenApi;
import com.github.elibracha.model.ChangedOpenApiRenderList;
import com.github.elibracha.output.*;
import com.github.elibracha.processors.ContextProcessor;
import org.apache.commons.cli.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class Main {

    static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String... args) {
        Boolean isObject=false;
        Options options = new Options();
        options.addOption(Option.builder("h").longOpt("help").desc("print this message").build());
        options.addOption(
                Option.builder().longOpt("version").desc("print the version information and exit").build());
        options.addOption(
                Option.builder()
                        .longOpt("state")
                        .desc("Only output diff state: no_changes, incompatible, compatible")
                        .build());
        options.addOption(
                Option.builder()
                        .longOpt("fail-on-incompatible")
                        .desc("Fail only if API changes broke backward compatibility")
                        .build());
        options.addOption(Option.builder().longOpt("trace").desc("be extra verbose").build());
        options.addOption(
                Option.builder().longOpt("debug").desc("Print debugging information").build());
        options.addOption(
                Option.builder().longOpt("info").desc("Print additional information").build());
        options.addOption(Option.builder().longOpt("warn").desc("Print warning information").build());
        options.addOption(Option.builder().longOpt("error").desc("Print error information").build());
        options.addOption(Option.builder().longOpt("off").desc("No information printed").build());
        options.addOption(Option.builder().longOpt("object").desc("print summary object as json").build());
        options.addOption(
                Option.builder("l")
                        .longOpt("log")
                        .hasArg()
                        .argName("level")
                        .desc("use given level for log (TRACE, DEBUG, INFO, WARN, ERROR, OFF). Default: ERROR")
                        .build());
        options.addOption(
                Option.builder()
                        .longOpt("header")
                        .hasArgs()
                        .numberOfArgs(2)
                        .valueSeparator()
                        .argName("property=value")
                        .desc("use given header for authorisation")
                        .build());
        options.addOption(
                Option.builder()
                        .longOpt("query")
                        .hasArgs()
                        .numberOfArgs(2)
                        .valueSeparator()
                        .argName("property=value")
                        .desc("use query param for authorisation")
                        .build());
//        options.addOption(
//                Option.builder("o")
//                        .longOpt("output")
//                        .hasArgs()
//                        .numberOfArgs(2)
//                        .valueSeparator()
//                        .argName("format=file")
//                        .desc("use given format (html, markdown) for output in file")
//                        .build());
        options.addOption(
                Option.builder()
                        .longOpt("markdown")
                        .hasArg()
                        .argName("file")
                        .desc("export diff as markdown in given file")
                        .build());
        options.addOption(
                Option.builder()
                        .longOpt("html")
                        .hasArg()
                        .argName("file")
                        .desc("export diff as html in given file")
                        .build());
        options.addOption(
                Option.builder()
                        .longOpt("json")
                        .hasArg()
                        .argName("file")
                        .desc("export diff as json in given file")
                        .build());
        options.addOption(
                Option.builder()
                        .longOpt("object")
                        .desc("export summary object as a json file")
                        .build());
        options.addOption(
                Option.builder("i")
                        .valueSeparator()
                        .type(Boolean.class)
                        .optionalArg(true)
                        .desc("activate diff ignore")
                        .build());
        options.addOption(
                Option.builder()
                        .longOpt("ignore-path")
                        .hasArg()
                        .argName("file")
                        .desc("path to diff ignore file")
                        .build());
        // create the parser
        CommandLineParser parser = new DefaultParser();
        try {
            // parse the command line arguments
            CommandLine line = parser.parse(options, args);
            if (line.hasOption("h")) { // automatically generate the help statement
                printHelp(options);
                System.exit(0);
            }
            String logLevel = "ERROR";
            if (line.hasOption("off") || (line.hasOption("json")) || (line.hasOption("object"))) {
                logLevel = "OFF";
            }
            if (line.hasOption("error")) {
                logLevel = "ERROR";
            }
            if (line.hasOption("warn")) {
                logLevel = "WARN";
            }
            if (line.hasOption("info")) {
                logLevel = "INFO";
            }
            if (line.hasOption("debug")) {
                logLevel = "DEBUG";
            }
            if (line.hasOption("trace")) {
                logLevel = "TRACE";
            }
            if (line.hasOption("object")) {
                isObject=true;
            }
            if (line.hasOption("log")) {
                logLevel = line.getOptionValue("log");
                if (!logLevel.equalsIgnoreCase("TRACE")
                        && !logLevel.equalsIgnoreCase("DEBUG")
                        && !logLevel.equalsIgnoreCase("INFO")
                        && !logLevel.equalsIgnoreCase("WARN")
                        && !logLevel.equalsIgnoreCase("ERROR")
                        && !logLevel.equalsIgnoreCase("OFF")) {
                    throw new ParseException(
                            String.format(
                                    "Invalid log level. Excepted: [TRACE, DEBUG, INFO, WARN, ERROR, OFF]. Given: %s",
                                    logLevel));
                }
            }
            if (line.hasOption("state")) {
                logLevel = "OFF";
            }
            LogManager.getRootLogger().setLevel(Level.toLevel(logLevel));
                 if (line.getArgList().size() < 2) { //   && (!(isObject)))
                     throw new ParseException("Missing arguments");
                 }
            String oldPath = line.getArgList().get(0);
            String newPath = line.getArgList().get(1);

            ChangedOpenApi result = OpenApiCompare.fromLocations(oldPath, newPath);
            ChangedOpenApiRenderList changedOpenApiRenderList=new ChangedOpenApiRenderList(result);
            if (line.hasOption("i")) {
                ContextProcessor contextProcessor = null;

                if (line.hasOption("ignore-path"))
                    contextProcessor = new ContextProcessor(line.getOptionValue("ignore-path"));
                else {
                    contextProcessor = new ContextProcessor();
                }

                result = contextProcessor.process(result);
            }

            ConsoleRender consoleRender = new ConsoleRender();
            if (!logLevel.equals("OFF")) {
                System.out.println(consoleRender.render(result));
            }
            HtmlRender htmlRender = new HtmlRender();
            MarkdownRender mdRender = new MarkdownRender();
            JsonRender jsonRender = new JsonRender();
            SumJsonRender sumJsonRender=new SumJsonRender();

            String output = null;
            String outputFile = null;

            if (line.hasOption("html")) {
                output = htmlRender.render(result);
                outputFile = line.getOptionValue("html");
            }
            if (line.hasOption("markdown")) {
                output = mdRender.render(result);
                outputFile = line.getOptionValue("markdown");
            }
            if (line.hasOption("json")) {
                output = jsonRender.render(result);
                outputFile = line.getOptionValue("json");
            }
            if (line.hasOption("object")) {
                output = sumJsonRender.render(changedOpenApiRenderList);
                outputFile = line.getOptionValue("object");
                System.out.println(output);
            }

            if (line.hasOption("output")) {
                String[] outputValues = line.getOptionValues("output");
                if (outputValues[0].equalsIgnoreCase("markdown")) {
                    output = mdRender.render(result);
                } else if (outputValues[0].equalsIgnoreCase("html")) {
                    output = htmlRender.render(result);
                } else if (outputValues[0].equalsIgnoreCase("json")) {
                    output = jsonRender.render(result);
                } else if (outputValues[0].equalsIgnoreCase("object")) {
                    output = sumJsonRender.render(changedOpenApiRenderList);
                } else {
                    throw new ParseException("Invalid output format");
                }
                outputFile = outputValues[1];
            }

            if (output != null && outputFile != null) {
                File file = new File(outputFile);
                logger.debug("Output file: {}", file.getAbsolutePath());
                try {
                    FileUtils.writeStringToFile(file, output);
                } catch (IOException e) {
                    logger.error("Impossible to write output to file {}", outputFile, e);
                    System.exit(2);
                }
            }

            if (line.hasOption("state")) {
                System.out.println(result.isChanged().getValue());
                System.exit(0);
            } else if (line.hasOption("fail-on-incompatible")) {
                System.exit(result.isCompatible() ? 0 : 1);
            } else {
                System.exit(result.isUnchanged() ? 0 : 1);
            }
        } catch (ParseException e) {
            // oops, something went wrong
            System.err.println("Parsing failed. Reason: " + e.getMessage());
            printHelp(options);
            System.exit(2);
        } catch (Exception e) {
            System.err.println(
                    "Error: " + e.getMessage());
            System.exit(2);
        }
    }

    public static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("openapi-diff <old> <new>", options);
    }
}
