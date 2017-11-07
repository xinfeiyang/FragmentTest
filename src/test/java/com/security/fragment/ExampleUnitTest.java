package com.security.fragment;

import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;

public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testAdd() {
        System.out.println(new Random().nextInt(25));
        System.out.println(2 + 3);
    }
}