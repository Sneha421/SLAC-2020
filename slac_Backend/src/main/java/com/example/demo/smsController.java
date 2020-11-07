package com.example.demo;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

@RestController
@CrossOrigin
public class smsController
{

    @PostMapping(path = "/sendSMS")
    public void sendSMS(String phoneNumber, int positionNo)
    {
        Twilio.init("ACe9da3a7a378288ac8b861de80838589d","4b7a5fdcc287d76107ed52d593aa7c4c");

        Message message = null;

        if(positionNo == 1)
        {
            message = Message.creator(
                    new com.twilio.type.PhoneNumber("+91"+phoneNumber),//To
                    new com.twilio.type.PhoneNumber("+12057782710"), //From
                    "You are next in line, please hurry!")
                    .create();
        }




        System.out.println(message.getSid());


    }
}
