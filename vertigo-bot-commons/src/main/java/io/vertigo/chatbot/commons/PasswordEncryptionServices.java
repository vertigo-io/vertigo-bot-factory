package io.vertigo.chatbot.commons;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Inject;

import io.vertigo.core.node.component.Activeable;
import io.vertigo.core.node.component.Component;
import io.vertigo.core.param.ParamManager;

import static io.vertigo.chatbot.commons.ChatbotUtils.ENCRYPTION_ALGO;

/**
 * @author cmarechal
 * @created 29/09/2022 - 15:46
 * @project vertigo-bot-factory
 */
public class PasswordEncryptionServices implements Component, Activeable {

    @Inject
    private ParamManager paramManager;

    private String encryptionPassword;

    private final SecureRandom secureRandom= new SecureRandom();

    private SecretKeyFactory secretKeyFactory;

    private Cipher cipher;

    private final Base64.Encoder base64Encoder = Base64.getEncoder();

    private final Base64.Decoder base64Decoder = Base64.getDecoder();

    @Override
    public void start() {
       initEncryptionParam();
    }

    private void initEncryptionParam() {
        if (encryptionPassword == null) {
            encryptionPassword = paramManager.getParam("ENCRYPTION_PASSWORD").getValueAsString();
        }
        try {
            if (secretKeyFactory == null) {
                secretKeyFactory = SecretKeyFactory.getInstance(ENCRYPTION_ALGO);
            }
            if (cipher == null) {
                cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            }
        } catch (final NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    public String encryptPassword(final String password) {
        try {
            initEncryptionParam();
            final byte[] salt = generateRandom();
            final byte[] iv = generateRandom();
            final KeySpec spec = new PBEKeySpec(encryptionPassword.toCharArray(), salt, 65536, 256);
            final SecretKey secretKey = new SecretKeySpec(secretKeyFactory.generateSecret(spec)
                    .getEncoded(), "AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));
            final byte[] cipherText = cipher.doFinal(password.getBytes());
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(salt);
            outputStream.write(iv);
            outputStream.write(cipherText);
            return base64Encoder.encodeToString(outputStream.toByteArray());
        } catch (final IllegalBlockSizeException | InvalidAlgorithmParameterException |
                       BadPaddingException | InvalidKeyException | InvalidKeySpecException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String decryptPassword(final String encryptedPassword) {
        initEncryptionParam();
        try {

            final byte[] ciphertext = base64Decoder.decode(encryptedPassword);
            final byte[] salt = Arrays.copyOfRange(ciphertext, 0, 16);
            final byte[] iv = Arrays.copyOfRange(ciphertext, 16, 32);
            final byte[] ct = Arrays.copyOfRange(ciphertext, 32, ciphertext.length);

            final KeySpec spec = new PBEKeySpec(encryptionPassword.toCharArray(), salt, 65536, 256);
            final SecretKey secretKey = new SecretKeySpec(secretKeyFactory.generateSecret(spec)
                    .getEncoded(), "AES");

            cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
            final byte[] plainText = cipher.doFinal(ct);
            return new String(plainText);
        } catch (final InvalidAlgorithmParameterException | IllegalBlockSizeException |
                       BadPaddingException | InvalidKeyException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }

    }

    private byte[] generateRandom() {
        final byte[] random = new byte[16];
        secureRandom.nextBytes(random);
        return random;
    }

    @Override
    public void stop() {

    }
}
