package com.belong.others;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 用NIO来实现读取文件
 * <describe>db.txt是生产库中的表名</describe>
 * <title>用于读取D:\tmp目录下的账管表模型中的模型</title>
 * Created by belong on 2017/4/12.
 */
public class ABInModelCheck {
    public static HashMap<String,Integer> username = new HashMap<>();
    public static void main(String[] args) {
        HashMap<String, Object> map = ModelTableNum();
        //System.out.println("========================================================");
        HashSet<String> set = (HashSet<String>) map.get("set");
        ArrayList<String> list = (ArrayList<String>) map.get("list");
        //System.out.println("========================================================");
        Collections.sort(list);
        //System.out.println("set[不重复的模型]："+set.size());
        //System.out.println("list[可能重复的模型]："+list.size());
        //System.out.println("模型中一共有"+set.size()+"个表");
        //System.out.println("检测数据库中的表是否在模型中");
        URL url = ABInModelCheck.class.getClassLoader().getResource("txt/db.txt");
        HashSet<String> set_tmp = dbAndModelCmp(url.getPath(),set);
        //System.out.println("检查模型中有而数据库中没有的表");
        //System.out.println("========================================================");
        for(String i:list){
            if(!set_tmp.contains(i.trim())){
                //System.out.println(i);
            }
        }
        //System.out.println("========================================================");
        //System.out.println("模型中的属组名包括："+username);


    }

    /**
     * 用于计算和记录账管模型中的表的数量
     * @return
     */
    public static HashMap<String,Object> ModelTableNum(){
        String root = "D:\\tmp";
        Path path = Paths.get(root);
        File file = path.toFile();
        File[] files = file.listFiles();
        HashSet<String> set = new HashSet<>();
        ArrayList<String> list = new ArrayList<>();
        HashMap<String,Object> map = new HashMap<>();
        int sum = 0;
        for(File i: files){
            String file_path = root+File.separator+i.getName().toString();
            File tmp_file = new File(file_path);
            //System.out.println(tmp_file.getName()+"=================begin");
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(tmp_file),"GBK"));
                String buffer = null;
                // 循环遍历tmp下的*.sql文件内容
                int count = 0;
                StringBuffer stringBuffer = new StringBuffer();
                String context = "";
                // 默认是开发匹配的
                boolean flag = true;
                while((context = br.readLine())!=null){
                    String doc_regex_beg = "/\\*\\*(.*)";
                    String doc_regex_end = "(.*)\\*\\*/";
                    Pattern doc_pattern_beg = Pattern.compile(doc_regex_beg,Pattern.DOTALL);
                    Matcher doc_matter_beg = doc_pattern_beg.matcher(context);
                    Pattern doc_pattern_end = Pattern.compile(doc_regex_end,Pattern.DOTALL);
                    Matcher doc_matter_end = doc_pattern_end.matcher(context);
                    // 注释开头就关闭
                    if(doc_matter_beg.find()){
                        flag = false;
                    }
                    // 注释结束就打开
                    if(doc_matter_end.find()){
                        flag = true;
                    }
                    //System.out.println(flag);
                    if(flag){
                        String regex = "CREATE(.*)TABLE(.*)\\.(.*)";
                        Pattern pattern = Pattern.compile(regex,Pattern.CASE_INSENSITIVE);
                        Matcher matcher = pattern.matcher(context);
                        if(matcher.find()){
                            set.add(matcher.group(3));
                            count++;
                        }
                    }
                }
                // 负责计数
                sum += count;
                //String regex = "CREATE(.*)TABLE (.*)\\.(.*)[(|\n|  (| (]*";
                //Pattern pattern = Pattern.compile(regex);
                //Matcher matcher = pattern.matcher(buffer.toUpperCase());
                //
                //if(matcher.find()){
                //    count++;
                //    String user = matcher.group(2);
                //    if(username.containsKey(user)){
                //        int num = username.get(user);
                //        num++;
                //        username.put(user,num);
                //    } else {
                //        username.put(user,1);
                //    }
                //    String tmp_str = matcher.group(3);
                //    //System.out.println(tmp_str);
                //    set.add(tmp_str.trim());
                //    list.add(tmp_str.trim());
                //}
                //System.out.println(i.getName()+"的表数量是"+count);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //System.out.println(tmp_file.getName()+"=================end");
        }
        System.out.println(set.size());
        System.out.println(set);
        map.put("set",set);
        map.put("list",list);
        return map;
    }

    /**
     * 用于比较数据库和账管模型的表数量
     * @param db_file 数据库中的表名称的文件名
     * @param set 账管模型中表的名称
     * @return 返回生产库中的表
     */
    public static HashSet<String> dbAndModelCmp(String db_file,HashSet<String> set){
        File file = new File(db_file);
        ArrayList<String> list = new ArrayList<>();
        HashSet<String> set_tmp = new HashSet<>();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String buffer;
            while ((buffer = br.readLine())!= null){
                list.add(buffer.trim());
                set_tmp.add(buffer.trim());
            }
            int count = 0;
            //System.out.println("========================================================");
            //System.out.println("DB库中有但是模型中没有的表是：");
            for (String i:list){
                if(!set.contains(i)){
                    count++;
                    //System.out.println(i);
                }
            }
            //System.out.println("========================================================");
            //System.out.println("一共有"+count+"个表不存在");
            //System.out.println("========================================================");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return set_tmp;
    }
}
