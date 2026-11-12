package br.unasp.zela.service;

import org.springframework.stereotype.Component;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * RNF04: senhas guardadas com hash PBKDF2 + salt aleatório.
 * Formato salvo: iteracoes:salt:hash (Base64).
 */
@Component
public class SenhaHasher {

    private static final int ITERACOES = 65_536;
    private static final int TAMANHO_BITS = 256;
    private final SecureRandom random = new SecureRandom();

    public String gerar(String senha) {
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        byte[] hash = pbkdf2(senha.toCharArray(), salt, ITERACOES);
        return ITERACOES + ":" + b64(salt) + ":" + b64(hash);
    }

    public boolean confere(String senha, String armazenado) {
        String[] partes = armazenado.split(":");
        int iteracoes = Integer.parseInt(partes[0]);
        byte[] salt = Base64.getDecoder().decode(partes[1]);
        byte[] esperado = Base64.getDecoder().decode(partes[2]);
        byte[] calculado = pbkdf2(senha.toCharArray(), salt, iteracoes);
        return MessageDigest.isEqual(esperado, calculado);
    }

    private byte[] pbkdf2(char[] senha, byte[] salt, int iteracoes) {
        try {
            PBEKeySpec spec = new PBEKeySpec(senha, salt, iteracoes, TAMANHO_BITS);
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Falha ao gerar hash de senha", e);
        }
    }

    private static String b64(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }
}
