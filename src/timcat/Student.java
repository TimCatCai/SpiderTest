package timcat;

public class Student {
    private String studentId;
    private String name;
    private String sex;
    private String departmentId;
    private String birthDay;

    public Student(String studentId, String name, String sex, String departmentId, String birthDay) {
        this.studentId = studentId;
        this.name = name;
        this.sex = sex;
        this.departmentId = departmentId;
        this.birthDay = birthDay;
    }

    public String getStudentId() {
        return "'" + studentId + "'";
    }

    public String getName() {
        return "'" + name + "'";
    }

    public String getSex() {
        return "'" + sex + "'";
    }

    public String getDepartmentId() {
        return "'" + departmentId + "'";
    }

    public String getBirthDay() {
        return "CONVERT(VARCHAR(40)," + DataUtils.stringType(birthDay) + ",23)";
    }

    @Override
    public String toString() {
        return DataUtils.formatOneTuple(getStudentId(), getName(), getSex(), getDepartmentId(), getBirthDay());
    }
}
