package org.khegori;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TelegramBot extends TelegramLongPollingBot {

    private static String token;
    private static String name;

    public void configureBot(String token, String name) throws SQLException, IOException, ClassNotFoundException {

        this.token = token;
        this.name = name;
    }


    //Имя бота
    @Override
    public String getBotUsername() {
        return this.name;
    }

    //Токен
    @Override
    public String getBotToken() {
        return this.token;
    }

    @Override
    public void onUpdateReceived(Update update) {

        try {

            //если текстовое сообщение
            if (update.hasMessage() && update.getMessage().hasText()) {

                Message inputMessage = update.getMessage();
                String chat_id = update.getMessage().getChatId().toString();
                String client_message = inputMessage.getText().toLowerCase().trim();

                SendMessage message = new SendMessage();

                String message_out ;

                if (client_message.equals("/start".toLowerCase().trim())) {
                    message_out = "Добрый день, скажите чего вы хотите";
                    message.setChatId(chat_id);
                    message.setText(message_out);
                    setButtons(message);
                    try {
                        execute(message); // Метод отправки сообщения
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }

                String str1 = "хочу участвовать в розыгрыше".toLowerCase().trim();

                if (client_message.equals(str1)) {
                    message.setChatId(chat_id);
                    message_out = "Сделайте ваш выбор";
                    message.setText(message_out);
                    setChangeButtons(message);
                    try {
                        execute(message); // Метод отправки сообщения
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }

                if (client_message.startsWith("change")) {
                    setButtons(message);
//                    clearButton(message);
                    message.setChatId(chat_id);
                    message_out = "Вы победили!";
                    message.setText(message_out);
                    try {
                        execute(message); // Метод отправки сообщения
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // клавиатура дефолта
    private void setButtons(SendMessage sendMessage) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);

        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);

        List<KeyboardRow> keyboardRowList = new ArrayList<>();
        KeyboardRow keyboardRow = new KeyboardRow();

        //собственно сами кнопки
        keyboardRow.add(new KeyboardButton("Хочу участвовать в розыгрыше"));

        keyboardRowList.add(keyboardRow);
        replyKeyboardMarkup.setKeyboard(keyboardRowList);
    }

    // клавиатура выбора
    private void setChangeButtons(SendMessage sendMessage) {

        List<String> changeList = List.of("change 1", "change 2", "change 3");


        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);

        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        List<KeyboardRow> keyboardRowList = new ArrayList<>();

        for (String change : changeList) {

            KeyboardRow keyboardRow = new KeyboardRow();

            //собственно сами кнопки
            keyboardRow.add(new KeyboardButton(change));
            keyboardRowList.add(keyboardRow);
            replyKeyboardMarkup.setKeyboard(keyboardRowList);
        }
    }

//    // установка пустой клавиатуры
//    private void clearButton(SendMessage sendMessage) {
//        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
//        sendMessage.setReplyMarkup(replyKeyboardMarkup);
//
//        replyKeyboardMarkup.setSelective(true);
//        replyKeyboardMarkup.setResizeKeyboard(true);
//        replyKeyboardMarkup.setOneTimeKeyboard(true);
//
//        List<KeyboardRow> keyboardRowList = new ArrayList<>();
//        KeyboardRow keyboardRow = new KeyboardRow();
//        keyboardRow.add(new KeyboardButton(""));
//
//        keyboardRowList.add(keyboardRow);
//        replyKeyboardMarkup.setKeyboard(keyboardRowList);
//    }

}
