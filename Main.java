package com.company;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class Main {
    static String pathXML;
    static String pathData;
    static String pathReport;

    static List<String> numbers = new ArrayList<>();
    static List<String> dates = new ArrayList<>();
    static List<String> persons = new ArrayList<>();
    static List<String> numbersIterOne = new ArrayList<>();
    static List<String> dateIterOne = new ArrayList<>();
    static List<String> personsIterOne = new ArrayList<>();
    static List<String> numbersIterTwo = new ArrayList<>();
    static List<String> datesIterTwo = new ArrayList<>();
    static List<String> personsIterTwo = new ArrayList<>();
    static List<String> numbersIterThree = new ArrayList<>();
    static List<String> datesIterThree = new ArrayList<>();
    static List<String> personsIterThree = new ArrayList<>();

    static List<String> allFromXML = new ArrayList<>();

    static String emptyCol1;
    static String emptyCol2;
    static String emptyCol3;

    static String[] contentFromXML;
    static String[] separateElements;

    static int numberColWidth;
    static int dateColWidth;
    static int personColWidth;
    static int stringsOnPage;
    static int symbolsInString;

    static String column1name;
    static String column2name;
    static String column3name;
    static BufferedReader reader;
    static ArrayList<String> dataInStrings;

    public static void main(String[] args) throws Exception {
        pathXML = "settings.xml";
        pathData = "source-data.tsv";
        pathReport = "report.txt";

        readXML ();
        readData ();
        separateData ();
        createEmptyCells ();
        iterateOne ();
        iterateTwo ();
        iterateThree ();
        addSpacesInResult ();
        writeFile ();
    }

    static void readXML () {
        File xmlFile = new File(pathXML);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;

        try {
            builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);
            document.getDocumentElement().normalize();
            Node nodeListCol = document.getFirstChild();
            do {
                allFromXML.add(nodeListCol.getTextContent());
                nodeListCol = nodeListCol.getNextSibling();
            } while (nodeListCol != null);
        } catch (Exception exc) {
            exc.printStackTrace();
        }

        for (int i = 0; i < allFromXML.size(); i++) {
            allFromXML.set(i, allFromXML.get(i).replaceAll("\t", " "));
            allFromXML.set(i, allFromXML.get(i).replaceAll("[\\s]{2,}", " "));
            allFromXML.set(i, allFromXML.get(i).trim());
        }

        contentFromXML = allFromXML.get(0).split(" ");

        numberColWidth = Integer.parseInt(contentFromXML[3]);
        dateColWidth = Integer.parseInt(contentFromXML[5]);
        personColWidth = Integer.parseInt(contentFromXML[7]);
        stringsOnPage = Integer.parseInt(contentFromXML[1]);
        symbolsInString = Integer.parseInt(contentFromXML[0]);
        column1name = addSpaces(contentFromXML[2], numberColWidth);
        column2name = addSpaces(contentFromXML[4], dateColWidth);
        column3name = addSpaces(contentFromXML[6], personColWidth);
    }

    static void readData () throws IOException {
        reader = new BufferedReader(new FileReader(pathData));
        String str1;
        dataInStrings = new ArrayList<>();
        while ((str1 = reader.readLine()) != null) {
            if (!str1.isEmpty()) {
                dataInStrings.add(str1);
            }
        }
    }

    static void separateData () {
        String data = String.join("\t", dataInStrings);
        separateElements = data.split("\t");

        for (int i = 0; i < separateElements.length; i = i + 3) {
            numbers.add(separateElements[i]);
            dates.add(separateElements[i+1]);
            persons.add(separateElements[i+2]);
        }
    }

    static String addSpaces(String oldString, int number) {
        oldString = oldString + " ".repeat(Math.max(0, (number - oldString.length())));
        return oldString;
    }

    static void createEmptyCells () {
        emptyCol1 = emptyCol2 = emptyCol3 = "";
        emptyCol1 = addSpaces(emptyCol1, numberColWidth);
        emptyCol2 = addSpaces(emptyCol2, dateColWidth);
        emptyCol3 = addSpaces(emptyCol3, personColWidth);
    }

    static void iterateOne () {
        for (int i = 0; i < numbers.size(); i++) {
            if (persons.get(i).length() > personColWidth) {
                String[] splited = persons.get(i).split("(?=-)|(?= )");
                if (splited[0].trim().length() + splited[1].trim().length() < personColWidth) {
                    numbersIterOne.add(numbers.get(i));
                    dateIterOne.add(dates.get(i));
                    personsIterOne.add(splited[0] + splited[1]);
                    for (int j = 2; j < splited.length; j++) {
                        numbersIterOne.add(emptyCol1);
                        dateIterOne.add(emptyCol2);
                        personsIterOne.add(splited[j].trim());
                    }
                } else {
                    numbersIterOne.add(numbers.get(i));
                    dateIterOne.add(dates.get(i));
                    personsIterOne.add(splited[0]);
                    for (int j = 1; j < splited.length; j++) {
                        numbersIterOne.add(emptyCol1);
                        dateIterOne.add(emptyCol2);
                        personsIterOne.add(splited[j].trim());
                    }
                }
            } else {
                numbersIterOne.add(numbers.get(i));
                dateIterOne.add(dates.get(i));
                personsIterOne.add(persons.get(i));
            }
        }
    }

    static void iterateTwo () {
        for (int i = 0; i < numbersIterOne.size(); i++) {
            if (personsIterOne.get(i).length() > personColWidth) {
                numbersIterTwo.add(numbersIterOne.get(i));
                datesIterTwo.add(dateIterOne.get(i));
                personsIterTwo.add(personsIterOne.get(i).substring(0, personColWidth));

                numbersIterTwo.add(emptyCol1);
                datesIterTwo.add(emptyCol2);
                personsIterTwo.add(personsIterOne.get(i).substring(personColWidth));
            } else {
                numbersIterTwo.add(numbersIterOne.get(i));
                datesIterTwo.add(dateIterOne.get(i));
                personsIterTwo.add(personsIterOne.get(i));
            }
        }
    }

    static void iterateThree () {
        for (int i = 0; i < numbersIterTwo.size(); i++) {
            if (datesIterTwo.get(i).length() > dateColWidth) {
                String[] splited = datesIterTwo.get(i).split("(?=/)");
                numbersIterThree.add(numbersIterTwo.get(i));
                datesIterThree.add(splited[0] + splited[1]);
                personsIterThree.add(personsIterTwo.get(i));
                if (datesIterTwo.get(i + 1).compareTo(emptyCol2) == 0) {
                    datesIterTwo.set(i + 1, splited[2]);
                } else {
                    numbersIterThree.add(emptyCol1);
                    datesIterThree.add(splited[2]);
                    personsIterThree.add(emptyCol3);
                }
            } else {
                numbersIterThree.add(numbersIterTwo.get(i));
                datesIterThree.add(datesIterTwo.get(i));
                personsIterThree.add(personsIterTwo.get(i));
            }
        }
    }

    static void addSpacesInResult () {
        for (int i = 0; i < numbersIterThree.size(); i++) {
            if (numbersIterThree.get(i).length() < numberColWidth) {
                numbersIterThree.set(i, addSpaces(numbersIterThree.get(i), numberColWidth));
            }
        }

        for (int i = 0; i < datesIterThree.size(); i++) {
            if (datesIterThree.get(i).length() < dateColWidth) {
                datesIterThree.set(i, addSpaces(datesIterThree.get(i), dateColWidth));
            }
        }

        for (int i = 0; i < personsIterThree.size(); i++) {
            if (personsIterThree.get(i).length() < personColWidth) {
                personsIterThree.set(i, addSpaces(personsIterThree.get(i), personColWidth));
            }
        }
    }

    static void writeFile () {
        String lineSeparator = System.getProperty("line.separator");
        try(
                FileWriter writer = new FileWriter(pathReport, false)) {
            int count1 = 0;
            for (int i = 1; count1 < numbersIterThree.size(); i++) {
                if (i == 1) {
                    writer.write("| " + column1name + " | " + column2name + " | " + column3name + " |");
                    writer.write(lineSeparator);
                    for (int j = 0; j < symbolsInString; j++)
                        writer.write("-");
                    writer.write(lineSeparator);
                } else if (i == stringsOnPage) {
                    writer.write("~");
                    writer.write(lineSeparator);
                } else if (i == stringsOnPage + 1) {
                    i = 0;
                } else {
                    writer.write("| " + numbersIterThree.get(count1) + " | " + datesIterThree.get(count1) + " | " + personsIterThree.get(count1) + " |");
                    writer.write(lineSeparator);
                    if ((count1 + 1) < numbersIterThree.size()) {
                        if (!numbersIterThree.get(count1 + 1).equals(emptyCol1)) {
                            for (int l = 0; l < symbolsInString; l++)
                                writer.write("-");
                            writer.write(lineSeparator);
                        }
                    }
                    count1++;
                }
            }
        } catch(IOException ex)  {
            System.out.println(ex.getMessage());
        }
    }
}