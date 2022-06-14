package Main;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * The type Main.
 */
public class Main {

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     * @throws NoSuchAlgorithmException the no such algorithm exception
     * @throws IOException              the io exception
     */
    public static void main(String[] args) throws NoSuchAlgorithmException, IOException {

        //Initiate files
        byte [] my_photo = convertImageToByte(new File("Sample-Image.jpg"));
        byte [] my_audio = convertWavToByte(new File("Sample-Audio.wav"));
        byte [] my_text = convertTextToByte(new File("Sample-Text.txt"));

        //Generate Random IVector and SecretKey
        IvParameterSpec my_iv = generateIv();
        SecretKey my_key = generateKey(128);

        //Encrypt files or text
        byte[] my_photo_encrypted = encrypt(my_photo, my_iv, my_key);
        byte[] my_audio_encrypted = encrypt(my_audio, my_iv, my_key);
        byte[] my_text_encrypted = encrypt(my_text, my_iv, my_key);

        //Generate or print ENCRYPTED files
        concertByteToImage(my_photo_encrypted,"Image-Encrypted");
        convertByteToWav(my_audio_encrypted, "Audio-Encrypted");
        convertByteToText(my_text_encrypted, "Text-Encrypted");

        //Decrypt files and text
        byte[] my_code_decrypted = decrypt(my_photo_encrypted, my_iv, my_key);
        byte[] my_audio_decrypted = decrypt(my_audio_encrypted, my_iv, my_key);
        byte[] my_text_decrypted = decrypt(my_text_encrypted, my_iv, my_key);

        //Generate or print DECRYPTED files
        concertByteToImage(my_code_decrypted,"Image-Decrypted");
        convertByteToWav(my_audio_decrypted, "Audio-Decrypted");
        convertByteToText(my_text_decrypted, "Text-Decrypted");
    }

    /**
     * Encrypt byte [ ].
     *
     * @param value    the value
     * @param iv       the iv
     * @param skeySpec the skey spec
     * @return the byte [ ]
     */
    //Return type bytes in order to give it as parameter to decrypt()
    public static byte[] encrypt(byte[] value, IvParameterSpec iv, SecretKey skeySpec) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            return cipher.doFinal(value);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Decrypt byte [ ].
     *
     * @param encrypted the encrypted
     * @param iv        the iv
     * @param skeySpec  the skey spec
     * @return the byte [ ]
     */
    public static byte[] decrypt(byte[] encrypted, IvParameterSpec iv, SecretKey skeySpec) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] original = cipher.doFinal(encrypted);

            return original;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Generate iv iv parameter spec.
     *
     * @return the iv parameter spec
     */
    //Generate Initialization Vector
    public static IvParameterSpec generateIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    /**
     * Generate key secret key.
     *
     * @param n the n
     * @return the secret key
     * @throws NoSuchAlgorithmException the no such algorithm exception
     */
    //Generate SecretKey
    public static SecretKey generateKey(int n) throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(n);
        return keyGenerator.generateKey();
    }

    /**
     * Concert byte to image.
     *
     * @param bytes     the bytes
     * @param file_name the file name
     * @throws IOException the io exception
     */
    //Methods to convert byte arrays
    public static void concertByteToImage(byte[] bytes, String file_name) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(file_name + ".jpg")) {
            fos.write(bytes);

            System.out.println("image created");
        }
    }

    /**
     * Convert byte to wav.
     *
     * @param bytes     the bytes
     * @param file_name the file name
     * @throws IOException the io exception
     */
    public static void convertByteToWav (byte[] bytes, String file_name) throws IOException {

        try (FileOutputStream fos = new FileOutputStream(file_name + ".wav")) {
            fos.write(bytes);

            System.out.println("audio created");
        }
    }

    /**
     * Convert byte to text.
     *
     * @param bytes     the bytes
     * @param file_name the file name
     * @throws FileNotFoundException the file not found exception
     */
    public static void convertByteToText(byte[] bytes, String file_name) throws FileNotFoundException {
        try (FileOutputStream fos = new FileOutputStream(file_name + ".txt")) {
            fos.write(bytes);

            System.out.println("Text file created");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Convert image to byte byte [ ].
     *
     * @param my_file the my file
     * @return the byte [ ]
     * @throws IOException the io exception
     */
    //Methods to reconvert
    public static byte[] convertImageToByte(File my_file) throws IOException {

        BufferedImage bImage = ImageIO.read(my_file);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(bImage, "jpg", bos );
        byte [] photo = bos.toByteArray();
        return photo;
    }

    /**
     * Convert wav to byte byte [ ].
     *
     * @param my_file the my file
     * @return the byte [ ]
     * @throws IOException the io exception
     */
    public static byte[] convertWavToByte(File my_file) throws IOException {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(my_file));

        int read;
        byte[] buff = new byte[1024];
        while ((read = in.read(buff)) > 0)
        {
            out.write(buff, 0, read);
        }
        out.flush();

        byte[] audioBytes = out.toByteArray();

        return audioBytes;
    }

    /**
     * Convert text to byte byte [ ].
     *
     * @param my_text_file the my text file
     * @return the byte [ ]
     * @throws IOException the io exception
     */
    public static byte[] convertTextToByte(File my_text_file) throws IOException {
        byte[] text = Files.readAllBytes(my_text_file.toPath());
        return text;
    }
}