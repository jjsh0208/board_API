package com.ddong_kka.board_api.exception;

public class BoardAlreadyDeletedException extends RuntimeException{
    public BoardAlreadyDeletedException(String message) {
        super(message);
    }
}
