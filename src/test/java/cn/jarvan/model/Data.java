package cn.jarvan.model;

import cn.jarvan.annotation.CellName;

/**
 * <b><code>Data</code></b>
 * <p>
 * Description.
 * <p>
 * <b>Creation Time:</b> 2019/2/25 14:28.
 *
 * @author liuruojing
 * @since auto-report-word 0.1.0
 */
public class Data {
    @CellName("姓名")
    private String name;
    @CellName("性别")
    private String sex;
    @CellName("年龄")
    private int age;

    public Data(String name, String sex, int age) {
        this.name = name;
        this.sex = sex;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
