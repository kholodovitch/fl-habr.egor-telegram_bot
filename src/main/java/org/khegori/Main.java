package org.khegori;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.IOException;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {

        TelegramBot bot = new TelegramBot();

        // настройка бота
        try {
            bot.configureBot(args[0], args[1]);
        } catch (SQLException throwables) {
            System.out.println("SQL-ошибка настройки бота");
            throwables.printStackTrace();
            return;
        } catch (IOException e) {
            System.out.println("IO-ошибка настройки бота");
            e.printStackTrace();
            return;
        } catch (ClassNotFoundException e) {
            System.out.println("DriverFound-ошибка настройки бота");
            e.printStackTrace();
            return;
        }

        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(bot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}