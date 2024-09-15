package org.khegori;

import org.apache.commons.lang3.StringUtils;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class TelegramBot extends TelegramLongPollingBot {

    public static final String CMD_START = "/start";
    public static final String CMD_I_WANT_TO_PARTICIPATE_IN_THE_DRAWING = "Хочу участвовать в розыгрыше";
    public static final String[] CMD_CHANGE_ARRAY = new String[]{"change 1", "change 2", "change 3"};

    public static final String MSG_WHAT_YOU_WANT = "Добрый день, скажите чего вы хотите";
    public static final String MSG_MAKE_YOUR_CHOICE = "Сделайте ваш выбор";
    public static final String MSG_YOU_ARE_WIN = "Вы победили!";

    private final String name;

    public TelegramBot(String token, String name) {
        super(token);
        this.name = name;
    }

    /**
     * Имя бота
     */
    @Override
    public String getBotUsername() {
        return this.name;
    }

    @Override
    public void onUpdateReceived(Update update) {
        // если НЕ текстовое сообщение
        if (!update.hasMessage() || !update.getMessage().hasText())
            return;

        Message inputMessage = update.getMessage();
        String chatId = update.getMessage().getChatId().toString();
        String clientMessage = inputMessage.getText().trim();
        SendMessage message = new SendMessage();
        String messageOut;

        switch (clientMessage) {
            case CMD_START -> {
                messageOut = MSG_WHAT_YOU_WANT;
                setActionButtons(message);
            }
            case CMD_I_WANT_TO_PARTICIPATE_IN_THE_DRAWING -> {
                messageOut = MSG_MAKE_YOUR_CHOICE;
                setChangeButtons(message);
            }
            default -> {
                if (Arrays.asList(CMD_CHANGE_ARRAY).contains(clientMessage)) {
                    setActionButtons(message);
//                  clearButton(message);
                    messageOut = MSG_YOU_ARE_WIN;
                } else {
                    throw new IllegalStateException("Unexpected value: %s".formatted(clientMessage));
                }
            }
        }

        try {
            message.setChatId(chatId);
            message.setText(messageOut);
            execute(message); // Метод отправки сообщения
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    /**
     * кастомная клавиатура
     */
    private void setButtons(SendMessage message, IKeyboardProcessor processor) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        message.setReplyMarkup(replyKeyboardMarkup);

        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);

        List<KeyboardRow> keyboardRowList = new ArrayList<>();
        processor.process(keyboardRowList);
        replyKeyboardMarkup.setKeyboard(keyboardRowList);
    }

    /**
     * клавиатура дефолта
     */
    private void setActionButtons(SendMessage message) {
        setButtons(message, keyboardRowList -> {
            List<KeyboardButton> buttons = List.of(new KeyboardButton(CMD_I_WANT_TO_PARTICIPATE_IN_THE_DRAWING));
            keyboardRowList.add(new KeyboardRow(buttons));
        });
    }

    /**
     * клавиатура выбора
     */
    private void setChangeButtons(SendMessage message) {
        setButtons(message, keyboardRowList -> {
            Stream<KeyboardRow> rowsToAdd = Arrays
                    .stream(CMD_CHANGE_ARRAY)
                    .map(x -> {
                        List<KeyboardButton> buttons = List.of(new KeyboardButton(CMD_I_WANT_TO_PARTICIPATE_IN_THE_DRAWING));
                        return new KeyboardRow(buttons);
                    });
            keyboardRowList.addAll(rowsToAdd.toList());
        });
    }

    /**
     * установка пустой клавиатуры
     */
    private void clearButton(SendMessage message) {
        setButtons(message, keyboardRowList -> {
            List<KeyboardButton> buttons = List.of(new KeyboardButton(StringUtils.EMPTY));
            keyboardRowList.add(new KeyboardRow(buttons));
        });
    }

    @FunctionalInterface
    interface IKeyboardProcessor {
        void process(List<KeyboardRow> keyboardRowList);
    }
}
