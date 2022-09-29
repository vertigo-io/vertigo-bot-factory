package io.vertigo.chatbot.executor.services;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Inject;

import io.vertigo.chatbot.commons.ChatbotUtils;
import io.vertigo.core.node.component.Activeable;
import io.vertigo.core.node.component.Component;
import io.vertigo.core.param.ParamManager;

import static io.vertigo.chatbot.commons.ChatbotUtils.ENCRYPTION_ALGO;

/**
 * @author cmarechal
 * @created 29/09/2022 - 17:28
 * @project vertigo-bot-factory
 */
public class PasswordDecryptionServices implements Component, Activeable {

    @Inject
    private ParamManager paramManager;

    private IvParameterSpec ivParameterSpec;

    private SecretKey secretKey;

    @Override
    public void start() {
        final String salt = paramManager.getParam("ENCRYPTION_SALT").getValueAsString();
        final String masterPassword = paramManager.getParam("ENCRYPTION_PASSWORD").getValueAsString();
        ivParameterSpec = new IvParameterSpec(paramManager.getParam("ENCRYPTION_IV").getValueAsString().getBytes());

        try {
            final SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(ENCRYPTION_ALGO);
            final KeySpec spec = new PBEKeySpec(masterPassword.toCharArray(), salt.getBytes(), 65536, 256);
            secretKey = new SecretKeySpec(secretKeyFactory.generateSecret(spec)
                    .getEncoded(), "AES");
        } catch (final NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    public String decryptPassword(final String encryptedPassword) {
        return ChatbotUtils.decryptPassword(encryptedPassword, secretKey, ivParameterSpec);
    }

    @Override
    public void stop() {

    }
}
