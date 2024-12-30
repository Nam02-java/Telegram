package com.example.Telegram.controller.client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import static java.lang.System.out;

public class IOServerHandle {

    private Long maxMessageId = Long.MIN_VALUE;

    public void initialize(Socket socket, String userName) throws IOException {

        BufferedReader inputFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        DataOutputStream outputToServer = new DataOutputStream(socket.getOutputStream());
        BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));


        new Thread(() -> {
            String messageFromServer;
            try {
                while ((messageFromServer = inputFromServer.readLine()) != null) {

                    if (messageFromServer.startsWith("Max ID in history list: ")) {
                        // Get biggest ID from history list
                        maxMessageId = Long.parseLong(messageFromServer.substring("Max ID in history list: ".length()));
                    } else {

                        String[] parts = messageFromServer.split(" - Message ID: ");
                        String messageIdStr = parts[1];
                        Long messageId = Long.parseLong(messageIdStr);

                        if (messageId > maxMessageId) {
                            /**
                             * This is a current message in real-time chat
                             * Display it as a new message on the UI
                             */
                            out.println("current messages ( " + messageFromServer + " )");
                        } else {
                            /**
                             * This is a historical message from the message history
                             * Display it as part of the chat history on the UI
                             */
                            out.println("history messages ( " + messageFromServer + " )");
                        }
                    }

                }
            } catch (IOException e) {
                System.err.println("Connection to server lost.");
            }
        }).start();

        String messageToServer;
        while ((messageToServer = userInput.readLine()) != null) {
            outputToServer.writeBytes(userName + " : " + messageToServer + "\n");
        }
    }
}
