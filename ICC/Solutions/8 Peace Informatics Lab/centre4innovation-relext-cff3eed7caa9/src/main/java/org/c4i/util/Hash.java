package org.c4i.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Hash utils.
 * <pre>
 *  HASH FUNCTION       LENGTH   RESULT
 *  md5()                 |32| = 5d41402abc4b2a76b9719d911017c592
 *  md5Base64()           |24| = XUFAKrxLKna5cZ2REBfFkg==
 *  md5Base64safe()       |22| = XUFAKrxLKna5cZ2REBfFkg
 *  sha1Hex()             |40| = aaf4c61ddcc5e8a2dabede0f3b482cd9aea9434d
 *  sha1Base64()          |28| = qvTGHdzF6KLavt4PO0gs2a6pQ00=
 *  sha256Hex()           |64| = 2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824
 *  sha256Base64()        |44| = LPJNul+wow4m6DsqxbninhsWHlwfp0JecwQzYpOLmCQ=
 *
 *  hash("")               = da39a3ee...
 *  hash(null)             = null
 * </pre>
 *
 * For background info about SHA variants and truncating them, see:
 * http://crypto.stackexchange.com/questions/9435/is-truncating-a-sha512-hash-to-the-first-160-bits-as-secure-as-using-sha1
 *
 * <p>As a general rule, you should avoid SHA1 for new applications and instead go with one of the hash functions from the SHA-2 family.</p>
 *
 * <p>As far as truncating a hash goes, that's fine. It's explicitly endorsed by the NIST, and there are hash functions in the SHA-2 family that are simple truncated variants of their full brethren: SHA-256/224, SHA-512/224, SHA-512/256, and SHA-512/384, where SHA-xx/yy denotes a full-length SHA-xx truncated to yy bits. (Technically the starting algorithm constant is different for the truncated versions, as pointed out by Henno Brandsma, but this is somewhat orthogonal to the point I'm trying to make.)</p>
 *
 * <p><b>Are there any security implications for hashing and storing sensitive data like this?</b></p>
 * <p>As far as determining the sensitive material from the digest itself, you're safe. All secure modern-day cryptographic hashes have what is called preimage resistance, which essentially means that it is computationally infeasible to "reverse" the hash, if you will. So, your sensitive data's confidentiality won't be compromised by storing the digest. (Note: See Gordon Davisson's comment below about this; there are possible security implications in some scenarios.)</p>
 *
 * <p>Now, the real question is: why are you wanting to store the hash in the first place? Hopefully you are not using it to detect if the data is maliciously modified; that generally is the purview of a MAC, such as HMAC or CBC-MAC.</p>
 *
 * <p><b>Is it more or less secure than using the full SHA1 hash?</b></p>
 * <p>Much more secure, actually, if you care about collision resistance. There is a (theoretical) attack on SHA1 that finds collisions in 260 time, whereas truncating SHA-512 to 160 bits requires 280 time to find collisions (see the birthday attack). So, truncating one of the SHA-2 functions to 160 bits is around 220 times stronger when it comes to collision resistance.</p>
 *
 * <p><b>Is there an increased risk of hash collision when using the truncated version?</b></p>
 * <p>Increased risk over SHA1? No. Increased risk over using the full SHA-512 output? Yes.</p>
 *
 * <p>Truncating the output of a hash function always decreases its (theoretical) collision-resistance. In practice, it usually doesn't matter too much; for instance, 280 time is still pretty big. Still, if you used the full output of SHA-256, the same birthday attack would take 2128 time, which is totally out of reach.</p>
 *
 * <p>by truncating it a potential hacker wouldn't know which algorithm was used in the first place</p>
 * <p>Always assume the attacker knows everything about your algorithm/cryptosystem except for the secret keys. This is known as Kerckhoffs's principle. Why this is important is well-covered, so you should follow it.</p>
 *
 * <p><b>My understanding was that SHA1 would always produce a unique value, whereas there is a chance that the first 40 characters of a SHA512 output could appear many times.</b></p>
 * <p>SHA1 doesn't produce unique values. There are infinitely-many possible inputs to SHA1 (it takes a bitstring of any length), yet there are only 160 bits of output. By the pigeonhole principle, there have to be infinitely-many values that map to the same 160 bits of output. But despite that, no one has ever found a collision for SHA1. (Or for that matter, SHA-2.) But, again, there still exists a theoretical attack on SHA1, which is why cryptographers recommend against it.</p>
 *
 * MessageDigests are *not* thread safe, this class *is* by not reusing them among calls.
 * @author Arvid
 * @version 2-6-2015 - 20:38
 */
public class Hash {

    private static SecureRandom random = new SecureRandom();


    /**
     * 32 char hash
     * @param s input to be hashed
     * @return the hash
     */
    public static String md5Hex(String s){
        return s == null ? null : DigestUtils.md5Hex(s);
    }

    /**
     * 24 char hash
     * @param s input to be hashed
     * @return the hash
     */
    public static String md5Base64(String s){
        return s == null ? null :  Base64.encodeBase64String(DigestUtils.md5(s));
    }

    /**
     * Less than 24 char hash
     * @param s input to be hashed
     * @return the hash
     */
    public static String md5Base64safe(String s){
        return s == null ? null :  Base64.encodeBase64URLSafeString(DigestUtils.md5(s));
        // Alternatively...
        /*try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] digest = md5.digest(s.getBytes("UTF-8"));
            return Base64.encodeBase64URLSafeString(digest);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;*/
    }

    /**
     * 40 char hash
     * @param s input to be hashed
     * @return the hash
     */
    public static String sha1Hex(String s){
        return s == null ? null : DigestUtils.sha1Hex(s);
    }

    /**
     * 40 char hash
     * @param s input to be hashed
     * @param length the max length of the returnes string
     * @return the hash
     */
    public static String sha1HexTrim(String s, int length){
        return s == null ? null : DigestUtils.sha1Hex(s).substring(0, length);
    }

    /**
     * 28 char hash
     * @param s input to be hashed
     * @return the hash
     */
    public static String sha1Base64(String s){
        return s == null ? null : Base64.encodeBase64String(DigestUtils.sha1(s));
    }

    /**
     * 64 char hash
     * @param s input to be hashed
     * @return the hash
     */
    public static String sha256Hex(String s){
        return s == null ? null : DigestUtils.sha256Hex(s);
    }

    /**
     * 44 char hash
     * @param s input to be hashed
     * @return the hash
     */
    public static String sha256Base64(String s){
        return s == null ? null : Base64.encodeBase64String(DigestUtils.sha256(s));
    }


    /**
     * Generate secure random alphanumeric string of 26 chars. E.g.
     * <pre>
     *     i3ffmcqnmmqj1mk00kvip075pm
     *     1lu500eqlqa0a6h2k8io42beu2
     *     q9mk2a2fckfq6kggndh73bhuk3
     * </pre>
     * This works by choosing 130 bits from a cryptographically secure random bit generator, and encoding them in base-32.
     * 128 bits is considered to be cryptographically strong, but each digit in a base 32 number can encode 5 bits,
     * so 128 is rounded up to the next multiple of 5. This encoding is compact and efficient, with 5 random bits
     * per character. Compare this to a random UUID, which only has 3.4 bits per character in standard layout,
     * and only 122 random bits in total.
     *
     * If you allow session identifiers to be easily guessable (too short, flawed random number generator, etc.),
     * attackers can hijack other's sessions. Note that SecureRandom objects are expensive to initialize, so you'll
     * want to keep one around and reuse it.
     *
     * See: http://stackoverflow.com/questions/41107/how-to-generate-a-random-alpha-numeric-string/41156#41156
     * @return a random string
     */
    public static String randomString128bit(){
        return new BigInteger(130, random).toString(32);
    }

    /**
     * Generate secure random alphanumeric string of 52 chars
     * @see #randomString128bit()
     * @return a random string
     */
    public static String randomString256bit(){
        return new BigInteger(260, random).toString(32);
    }

    public static void main(String[] args) {
        if(args.length == 1){
            System.out.println("plain = " + args[0]);
            String salt = randomString256bit();
            System.out.println("salt = " + salt);
            String hashedPassword = sha256Hex(salt+args[0]);
            System.out.println("hashedPassword = " + hashedPassword);
            System.exit(0);
        }

        System.out.println("HASH FUNCTION       LENGTH   RESULT");
        System.out.printf("md5()                 |%2d| = %s\n",  md5Hex("hello").length(), md5Hex("hello"));
        System.out.printf("md5Base64()           |%2d| = %s\n",  md5Base64("hello").length(), md5Base64("hello"));
        System.out.printf("md5Base64safe()       |%2d| = %s\n",  md5Base64safe("hello").length(), md5Base64safe("hello"));
        System.out.printf("sha1Hex()             |%2d| = %s\n",  sha1Hex("hello").length(), sha1Hex("hello"));
        System.out.printf("sha1Base64()          |%2d| = %s\n",  sha1Base64("hello").length(), sha1Base64("hello"));
        System.out.printf("sha256Hex()           |%2d| = %s\n",  sha256Hex("hello").length(), sha256Hex("hello"));
        System.out.printf("sha256Base64()        |%2d| = %s\n",  sha256Base64("hello").length(), sha256Base64("hello"));
        System.out.println("====");
        System.out.println("sha1Hex(\"\")     = " + sha1Hex(""));
        System.out.println("sha1Hex(null)     = " + sha1Hex(null));
        System.out.println("====");
        System.out.println("random:");
        System.out.println(randomString128bit());
        System.out.println(randomString128bit());
        System.out.println(randomString128bit());
    }



}
