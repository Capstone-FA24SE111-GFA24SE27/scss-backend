package com.capstone2024.scss.domain.common.utils;

import java.util.Random;

public class RandomUtil {
    public static int[] getRandomStartEnd(int a, int b, int k) {
        if (b - a + 1 < k + 1) {
            throw new IllegalArgumentException("The range is too small to generate a valid start and end with the given k.");
        }

        Random random = new Random();

        // Chọn số bắt đầu (start)
        int start = random.nextInt(b - a - k + 1) + a;

        // Chọn số kết thúc (end), đảm bảo cách start ít nhất k đơn vị
        int end = random.nextInt(b - start - k + 1) + (start + k);

        return new int[]{start, end};
    }
}
