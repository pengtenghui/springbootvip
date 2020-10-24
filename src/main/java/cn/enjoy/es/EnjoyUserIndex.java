package cn.enjoy.es;

import org.springframework.data.elasticsearch.annotations.*;

//@Mapping(mappingPath="/json/user.json")
//@Setting(settingPath="/json/userSetting.json")
@Document(indexName = "user", type = "enjoyUser")
public class EnjoyUserIndex {
    private String id;

    private String passwd;

    @Field(type = FieldType.Text, analyzer = "ik", searchAnalyzer = "ik", store = true)
    private String username;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd == null ? null : passwd.trim();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }
}
