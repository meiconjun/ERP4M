package org.meiconjun.erp4m.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.poi.ss.usermodel.Workbook;
import org.meiconjun.erp4m.bean.MenuBean;
import org.meiconjun.erp4m.bean.RequestBean;
import org.meiconjun.erp4m.bean.User;
import sun.misc.Request;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Lch
 * @Title:
 * @Package
 * @Description:
 * @date 2020/4/518:43
 */
public class Test {

    /*public static long getBetweenDays(String begin_date, String end_date) throws ParseException {
        SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
        Date beginDate = sd.parse(begin_date);
        Date endDate = sd.parse(end_date);

        return (endDate.getTime() - beginDate.getTime()) / (1000*60*60*24);
    }
    public static String formatDateString(String date, String format) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date d = sdf.parse(date);
        SimpleDateFormat sdf2 = new SimpleDateFormat(format);
        return  sdf2.format(d);
    }

    public static void poiTest() throws IOException {
        List<String> headList = new ArrayList<>();
        headList.add("用户号");
        headList.add("用户名");
        headList.add("部门");
        List<String[]> dataList = new ArrayList<>();
        dataList.add(new String[]{"00001", "张三", "主板设计部"});
        dataList.add(new String[]{"00002", "李四", "测试部"});

        Workbook workbook = ExcelUtil.exportExcel(headList, dataList);

        File file = new File("D:\\testExcel.xlsx");
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        workbook.write(fileOutputStream);
        fileOutputStream.flush();
        fileOutputStream.close();
        workbook.close();
    }*/
    public static void main(String[] args) throws ParseException, IOException {
        /*RequestBean<User> requestBean = new RequestBean<User>();
        User m = new User();
        m.setUser_no("AaaAss");
        Map m2 = new HashMap();
        m2.put("a", "12323213321421444");
        List<User> l = new ArrayList<User>();
        l.add(m);
        requestBean.setBeanList(l);
        requestBean.setOperType("aaaaa");
        requestBean.setParamMap(m2);

        String json = CommonUtil.objToJson(requestBean);

        RequestBean bean = (RequestBean) CommonUtil.jsonToObj(json, RequestBean.class);

        System.out.println(CommonUtil.formatJson(CommonUtil.objToJson(bean)));*/
        /*String aaa = "{\n" +
                "        'beanList' : [{\n" +
                "            \"user_no\" : user_no,\n" +
                "            'pass_word' : pass_word\n" +
                "        }],\n" +
                "        'operType' : 'login',\n" +
                "        'paramMap' : {}\n" +
                "    }";
        Gson gson = new Gson();
        RequestBean re = (RequestBean) gson.fromJson(aaa, new TypeToken<RequestBean<User>>(){}.getType());
        User u = (User) re.getBeanList().get(0);*/
        /*SerialNumberGenerater se = SerialNumberGenerater.getInstance();
        System.out.println(se.generaterNextNumber());
        System.out.println(se.generaterNextNumber());
        System.out.println(se.generaterNextNumber());
        System.out.println(se.generaterNextNumber());
        System.out.println(se.generaterNextNumber());
        System.out.println(se.generaterNextNumber());
        System.out.println(se.generaterNextNumber());*/
        /*List<String> list = new ArrayList<String>();
        list.add("aa");
        list.add("bb");s
        System.out.println(list.contains("aa"));*/
//        String date = "20200531";
//        System.out.println(formatDateString(date, "yyyy-MM-dd HH:mm:ss"));
//        numOfSubarrays(new int[]{100,100,99,99});
//        System.out.println(Math.pow(10, 9) + 7);
//        "11".substring(0, 1);
//        System.out.println(method_3("0100111010", "01"));
//        System.out.println(1/2);
        /*ListNode l1 = new ListNode(2);
        l1.next = new ListNode(4);
        l1.next.next = new ListNode(3);
        ListNode l2 = new ListNode(5);
        l2.next = new ListNode(6);
        l2.next.next = new ListNode(4);
        addTwoNumbers(l1, l2);*/
        /*BigDecimal dou = new BigDecimal("1000000000000000000000000000001");
        BigDecimal dou2 = new BigDecimal("564");
        BigDecimal sum = dou.add(dou2);
        System.out.println(sum);*/
//        lengthOfLongestSubstring("abcabcbb");
        int ii = "123".charAt(1) - '0';
        System.out.println(ii);
        char[] arr = {'a', 'A'};
        System.out.println(Character.toUpperCase(arr[0]) == arr[1]);
    }
    public static int method_3(String string, String a) {
        int number = 0;
        while (string.indexOf(a) >= 0) {
            int beginIndex = string.indexOf(a);
            /*在string中剔除字符串a。
            但是要注意jajaveve的情况，在第一次去除字符串java后，java前面的字符为"ja"，后面的字符为“va”
            又重新组合为java。故，在连接前后字符串时，加入其它替换字符，本方法中，采用空格字符。
            */
            string = string.substring(0, beginIndex) + " " + string.substring(beginIndex + a.length());
            number++;
        }
        return number;
    }
    public static int numOfSubarrays(int[] arr) {
        int bNum = 1 << arr.length;
        int count = 0;
        for (int mark = 0; mark < bNum; mark++) {
            List<Integer> arr1 = new ArrayList();
            int innerCount = 0;
            for (int i = arr.length - 1; i >=0 ; i--) {
                if (((1<<i) & mark) != 0) {
                    arr1.add(arr[i]);
                    innerCount += arr[i];
                }

            }
            System.out.println(arr1);
            System.out.println(innerCount);
            if ((innerCount % 2) != 0) {
                count++;
            }
        }
        int ii = (int)Math.pow(10, 9) + 7;
        return count;
    }

    public static int numOfSubarrays2(int[] arr) {
        int count = 0;
        for (int i = 0; i < arr.length; i++) {
            int innerCount = 0;
            for (int j = i; j < arr.length; j++) {
                innerCount += arr[j];
                if ((innerCount % 2) != 0) {
                    count ++;
                }
            }
        }
        int ii = (int)Math.pow(10, 9) + 7;
        return count % ii;
    }

    public static String restoreString(String s, int[] indices) {
        char[] ss = s.toCharArray();
        StringBuffer sb = new StringBuffer();
        sb.append("a", 0, 1);
        System.out.println(sb.toString());
        return null;
    }

    public static ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        StringBuilder sb1 = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        do {
            sb1.insert(0, l1.val);
            l1 = l1.next;
        } while (l1 != null);
        do {
            sb2.insert(0, l2.val);
            l2 = l2.next;
        } while (l2 != null);
        BigDecimal num1 = new BigDecimal(sb1.toString());
        BigDecimal num2 = new BigDecimal(sb2.toString());
        String sumStr_ = num1.add(num2).toString();
        char[] sumStr = sumStr_.toCharArray();
        ListNode head = new ListNode(Integer.valueOf(String.valueOf(sumStr[0])));
        for (int i = 1; i < sumStr.length; i++) {
            ListNode tempNode = new ListNode(Integer.valueOf(String.valueOf(sumStr[i])));
            tempNode.next = head;
            head = tempNode;
        }
        return head;
    }

    public static class ListNode {
          int val;
          ListNode next;
          ListNode(int x) { val = x; }
  }

     /*public static class TreeNode {
         int val;
         TreeNode left;
         TreeNode right;
         TreeNode(int x) { val = x; }
     }
    public int maxDepth(TreeNode root) {
        // 思路：利用一个队列遍历二叉树，并记录最大层数，该层数就是最长路径
        List<TreeNode> tnList = new ArrayList<TreeNode>();
        int level = 0;// 层级计数器
        int rear = -1;//队列尾指针，负责将树节点入队
        int current = -1;//队列遍历指针
        int last = 0;//记录当前遍历层的最后节点位置
        tnList.add(root);//根节点入队
        ++rear;
        TreeNode temp;
        while (current < rear) {// 遍历队列
            temp = tnList.get(++current);
            if (temp.left != null) { //左子树入队
                tnList.add(temp.left);
                ++rear;
            }
            if (temp.right != null) {//右子树入队
                tnList.add(temp.right);
                ++rear;
            }
            if (current == last) {// 当前层遍历至最后一个节点，同时入队指针也讲下一层全部入队
                ++level;
                last = rear;
            }
        }
        return level;
    }*/

    public static int lengthOfLongestSubstring(String s) {
        char[] charArr = s.toCharArray();
        int countLast = 0;
        int countThis = 0;
        int mark = 0;
        String resStr = "";
        while (mark < charArr.length) {
            if (resStr.indexOf(charArr[mark]) == -1) {
                resStr += charArr[mark];
                ++countThis;
            } else {
                resStr = resStr.substring(resStr.indexOf(charArr[mark]) + 1) + charArr[mark];
                System.out.println(resStr);
                System.out.println(countThis);
                countThis = resStr.length();
                if (countThis >= countLast) {
                    countLast = countThis;
                }
            }
            ++mark;
        }
        return countLast;
    }

    /**
     * Definition for a binary tree node.
     * public class TreeNode {
     *     int val;
     *     TreeNode left;
     *     TreeNode right;
     *     TreeNode() {}
     *     TreeNode(int val) { this.val = val; }
     *     TreeNode(int val, TreeNode left, TreeNode right) {
     *         this.val = val;
     *         this.left = left;
     *         this.right = right;
     *     }
     * }
     */
    class Solution {
        public void flatten(TreeNode root) {

        }
    }
    public static class TreeNode {
         int val;
         TreeNode left;
         TreeNode right;
         TreeNode() {}
         TreeNode(int val) { this.val = val; }
         TreeNode(int val, TreeNode left, TreeNode right) {
             this.val = val;
             this.left = left;
             this.right = right;
         }
     }
    public void flatten(TreeNode root) {
    }

    public String addStrings(String num1, String num2) {
        int pointer1 = num1.length() - 1;
        int pointer2 = num2.length() - 1;
        int carry = 0;// 进位
        StringBuilder sb = new StringBuilder();
        while (pointer1 >= 0 || pointer2 >= 0 || carry > 0) {
            int n1 = pointer1 >= 0 ? num1.charAt(pointer1) : 0;
            int n2 = pointer2 >= 0 ? num2.charAt(pointer2) : 0;
            int result = n1 + n2 + carry;
            carry = result / 10;
            sb.insert(0, result % 10);
            pointer1--;
            pointer2--;
        }
        return sb.toString();
    }

    public List<List<Integer>> generate(int numRows) {
        List<List<Integer>> retList = new ArrayList<>();
        for (int i = 1; i <= numRows; i++) {
            List<Integer> tempList = new ArrayList<>();
            for (int j = 1; j<= i; j++) {
                int num = 1;
                if (j != 1 && j != i) {
                    List<Integer> tempList2 = retList.get(i-2);
                    num = tempList2.get(j - 2) + tempList2.get(j - 1);
                }
                tempList.add(num);
            }
            retList.add(tempList);
        }
        return retList;
    }
}
