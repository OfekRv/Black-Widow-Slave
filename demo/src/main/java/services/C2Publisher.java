package services;

import java.io.IOException;

import exceptions.BlackWidowException;

public interface C2Publisher {
    void publishMessage(String message) throws BlackWidowException;
}
