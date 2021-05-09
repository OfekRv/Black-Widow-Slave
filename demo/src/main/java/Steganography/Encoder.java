package Steganography;

public interface Encoder<E, M> {
    E encode(E source, M message);
}
