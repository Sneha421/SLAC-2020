package com.example.demo;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication
{
    public static final String ACCOUNT_SID = System.getenv("ACe9da3a7a378288ac8b861de80838589d");
    public static final String AUTH_TOKEN = System.getenv("4b7a5fdcc287d76107ed52d593aa7c4c");


    public static void main(String[] args)
    {

        SpringApplication.run(DemoApplication.class, args);
    }

}
