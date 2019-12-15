package timcat;

import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class MainTest {

    public static void main(String[] args) {
//        String namesListJsonFilePath = "E:\\Fdisk\\JavaCode\\SpiderTest\\src\\www.resgain.net\\namesListURL.json";
//        File file = new File(namesListJsonFilePath);
//        String nameJsonString = null;
//        try {
//            nameJsonString = FileUtils.readFileToString(file);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        Gson gson = new Gson();
//        Model model = gson.fromJson(nameJsonString, Model.class);
//        List<String> urlsList = model.getAllNameUrls();
//        String nameUrl;
//        StringBuilder result = new StringBuilder();
//        for (String url : urlsList) {
//            Logger logger = Logger.getLogger(MainTest.class.getName());
//            nameUrl = "http:" + url;
//            Spider.create(new OneSecondNameList(nameUrl, result))
//                    .addUrl(nameUrl)
//                    .addPipeline(new JsonFilePipeline("E:\\Fdisk\\JavaCode\\SpiderTest\\src"))
//                    .thread(4)
//                    .run();
//            logger.info(result.toString());
//        }
//        System.out.println(result.toString().length() / 4);
        Path path = Paths.get("E:\\Fdisk\\JavaCode\\SpiderTest\\names");
        Random random = new Random(System.currentTimeMillis());
        List<String> studentDataList = new ArrayList<>();
        List<String> studentNameList = new ArrayList<>();
        String insertHead = DataUtils.insertHead("STUDENT(STUDENT_ID,STUDENT_NAME,SEX,DEPARTMENT_ID,BIRTHDAY)");
        studentDataList.add(insertHead);
        int id = 100;
        int sqlServerInsertLimit = 1000;
        int nextLimit = sqlServerInsertLimit + id - 1;

        try {
            List<Path> paths = Files.walk(path).collect(Collectors.toList());
            for(Path p: paths){
                if (!Files.isDirectory(p)) {
                    String json = null;
                    try {
                        json= FileUtils.readFileToString(new File(p.toString()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    String[] nameList = DataUtils.getNameListFromJsonFile(json);
                    String studentId;
                    String name;
                    String sex;
                    String departmentId;
                    String birthDay;
                    for (String aName : nameList) {
                        studentId = DataUtils.addLeadingZero(id, "3117000000");
                        name = aName;
                        if(name.contains("'")){
                            continue;
                        }
                        studentNameList.add(name);
                        sex = DataUtils.convertSexCodeToSexString(random.nextInt(2));
                        departmentId = DataUtils.addLeadingZero(random.nextInt(17), "0000000000");
                        birthDay = DataUtils.dateFormat(1988 + random.nextInt(15), 1 + random.nextInt(12), 1 + random.nextInt(28));

                        // sql server insert语句每次只能插入1000条元组，所以每1千条数据要作为一次插入
                        // 插入的元组数已经到达sql server的上限，修改最后一个元组的逗号为分号，且重新插入一个的insertHead；
                        // 即每次只插入1000条元组
                        if(id == nextLimit){
                            studentDataList.add(new Student(studentId, name, sex, departmentId, birthDay).toString() + ";");
                            studentDataList.add(insertHead);
                            nextLimit += sqlServerInsertLimit;
                        }else{
                            studentDataList.add(new Student(studentId, name, sex, departmentId, birthDay).toString() + ",");
                        }

                        id ++;
                    }
                }
            }
            // 判断是否最后一条元组数据需要去掉逗号之后加上分号
            // 刚好到达边界，要去掉insert head
            if(id - 1 == nextLimit - sqlServerInsertLimit){
                studentDataList.remove(studentDataList.size() - 1);
            }else{
                String last = studentDataList.get(studentDataList.size() - 1);
                last = StringUtils.substring(last, 0, last.length() - 1) + ";";
                studentDataList.set(studentDataList.size() - 1, last);
            }
            File insertFile = new File("E:\\Fdisk\\JavaCode\\SpiderTest\\student_insert_file.txt");
            FileUtils.writeLines(insertFile, studentDataList);
            File namesFile = new File("files/names.txt");
            FileUtils.writeLines(namesFile, studentNameList);

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
