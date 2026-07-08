/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.greentrack.security;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.util.Base64;
import javax.crypto.SecretKey;

/**
 *
 * @author Leveno
 */
public class SecretKeyGenerator {
    public static void main(String[] args){
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        
        String secret = Base64.getEncoder()
                .encodeToString(key.getEncoded());
        
        //System.out.println("Secret Key: "+ secret);
    }
}
