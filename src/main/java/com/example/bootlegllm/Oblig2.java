package com.example.bootlegllm;/*
 
Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template*/

import java.util.*;
import java.io.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
/**
 *
 
@author erikr*/
public class data{
    public static something getData(){
        try{

            //kode
            String filnavn = "Fil.txt";
            File fil = new File(filnavn);
            Scanner leser = new Scanner(fil);
            StringBuilder tekst = new StringBuilder();
            while(leser.hasNext()){
                tekst.append(leser.next()).append(" ");
            }
            String[] ord = tekst.toString().replaceAll("[a-zA-Z,.?!\\s]", "").toLowerCase().split("\\s,.?!+");
            Pattern pattern;
            Matcher matcher;
            int[] antall = new int[5000];

            for (int i = 0; i<ord.length -2; i++) {
                pattern = Pattern.compile(ord[i] + " " + ord[i+1] + " " + ord[i + 2]);
                matcher = pattern.matcher(tekst.toString());

                while(matcher.find()){
                    antall[i]++;
                }
                if(antall[i] > 1)
                    System.out.println("The expression: " + ord[i] + " " + ord[i+1] + " " + ord[i + 2] + " has been found " + antall[i] + " times");
                //j++;
            }



        } catch(IOException e) {
            e.printStackTrace();
            System.out.println("Fail");
        }
    } // spy spy spy, dont prove im right
}
