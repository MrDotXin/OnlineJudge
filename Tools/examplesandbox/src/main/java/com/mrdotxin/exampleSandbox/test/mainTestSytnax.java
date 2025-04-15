package com.mrdotxin.exampleSandbox.test;

public class mainTestSytnax {
    

    public static void main(String[] args) {
        final Integer [] nums = {0};
        changeNumber(nums);
        

        System.out.println(nums[0]);
    }



    private static void changeNumber(Integer [] nums) {

        nums[0] = 1;
    }
}
